package com.example.project;

import org.springframework.http.HttpStatus;
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
@RequestMapping("/upload")
public class ImageController {

    private static final String UPLOAD_DIR = "uploaded-images/";

    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        String fileName = UUID.randomUUID().toString() + "." + getExtension(file.getOriginalFilename());
        Path targetLocation = Path.of(UPLOAD_DIR, fileName);

        try {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return ResponseEntity.ok().body(new UploadResponse(fileName, true));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UploadResponse("Upload failed", false));
        }
    }

    @GetMapping("/download-all")
    public ResponseEntity<byte[]> downloadAllImages() throws IOException {
        File folder = new File(UPLOAD_DIR);
        File[] files = folder.listFiles((dir, name) -> !name.startsWith(".") && !new File(dir, name).isDirectory());

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

    static class UploadResponse {
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
