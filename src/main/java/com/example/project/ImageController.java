package com.example.project;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class ImageController {

    private static final String UPLOAD_DIR = "uploaded-images/";

    // Endpoint to handle image upload
    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        // Generate a UUID filename
        String fileName = UUID.randomUUID().toString() + "." + getExtension(file.getOriginalFilename());
        Path targetLocation = Path.of(UPLOAD_DIR, fileName);

        try {
            // Ensure directory exists
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Save the file to disk
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return ResponseEntity.ok().body(new UploadResponse(fileName, true));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UploadResponse("Upload failed", false));
        }
    }

    // Helper method to get file extension
    private String getExtension(String filename) {
        return StringUtils.getFilenameExtension(filename);
    }

    // DTO to send success/failure response
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
