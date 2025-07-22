package com.example.project;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
public class ImageController {

    private final AppConfig config;

    public ImageController(AppConfig config) {
        this.config = config;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        String extension = getExtension(file.getOriginalFilename());
        if (extension == null || extension.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new UploadResponse("Invalid file extension", false));
        }

        String fileName = UUID.randomUUID().toString() + "." + extension;
        Path targetLocation = Path.of(config.getUploadDir(), fileName);

        try {
            File dir = new File(config.getUploadDir());
            if (!dir.exists() && !dir.mkdirs()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new UploadResponse("Could not create upload directory", false));
            }

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return ResponseEntity.ok(new UploadResponse(fileName, true));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UploadResponse("Upload failed", false));
        }
    }

    @GetMapping("/{prefix}/{filename:.+}")
    public ResponseEntity<byte[]> getImage(
            @PathVariable String prefix,
            @PathVariable String filename
    ) {
        if (!prefix.equals(config.getImageEndpointPrefix())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        try {
            Path file = Path.of(config.getUploadDir(), filename);
            if (!Files.exists(file) || Files.isDirectory(file)) {
                return ResponseEntity.notFound().build();
            }

            byte[] imageBytes = Files.readAllBytes(file);
            String contentType = Files.probeContentType(file);
            if (contentType == null || !contentType.startsWith("image/")) {
                contentType = MediaType.IMAGE_PNG_VALUE; // fallback
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{prefix}/download-all")
    public ResponseEntity<byte[]> downloadAllImages(@PathVariable String prefix) throws IOException {
        if (!prefix.equals(config.getImageEndpointPrefix())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        File folder = new File(config.getUploadDir());
        File[] files = folder.listFiles((dir, name) -> !name.startsWith(".") && new File(dir, name).isFile());

        if (files == null || files.length == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (File file : files) {
                zos.putNextEntry(new ZipEntry(file.getName()));
                Files.copy(file.toPath(), zos);
                zos.closeEntry();
            }
        }

        byte[] zipBytes = baos.toByteArray();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=all-images.zip")
                .header("Content-Type", "application/zip")
                .body(zipBytes);
    }


    private String getExtension(String filename) {
        return StringUtils.getFilenameExtension(filename);
    }

    public static class UploadResponse {
        private String filename;
        private boolean success;

        public UploadResponse(String filename, boolean success) {
            this.filename = filename;
            this.success = success;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }
    }
}
