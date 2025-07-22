package com.example.project;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "custom")
public class AppConfig {

    private String uploadDir;
    private String imageBasePath;
    private int serverPort;
    private String imageEndpointPrefix; // <-- New field

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public String getImageBasePath() {
        return imageBasePath;
    }

    public void setImageBasePath(String imageBasePath) {
        this.imageBasePath = imageBasePath;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getImageEndpointPrefix() {
        return imageEndpointPrefix;
    }

    public void setImageEndpointPrefix(String imageEndpointPrefix) {
        this.imageEndpointPrefix = imageEndpointPrefix;
    }
}
