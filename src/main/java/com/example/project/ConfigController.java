package com.example.project;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ConfigController {

    private final AppConfig config;

    public ConfigController(AppConfig config) {
        this.config = config;
    }

    @GetMapping("/config")
    public Map<String, String> getImageBasePath() {
        return Map.of(
            "imageBasePath", config.getImageBasePath(),
            "imageEndpointPrefix", config.getImageEndpointPrefix()
        );
    }

}
