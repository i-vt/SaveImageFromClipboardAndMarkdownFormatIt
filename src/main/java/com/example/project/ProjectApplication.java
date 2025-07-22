package com.example.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class ProjectApplication {

    public static void main(String[] args) throws Exception {
        // Extract config.path
        String configPath = null;
        for (String arg : args) {
            if (arg.startsWith("--config.path=")) {
                configPath = arg.substring("--config.path=".length());
                break;
            }
        }

        if (configPath == null) {
            throw new IllegalArgumentException("Missing --config.path argument");
        }

        // Load JSON config before Spring Boot starts
        ObjectMapper mapper = new ObjectMapper();
        AppConfig config = mapper.readValue(new File(configPath), AppConfig.class);

        // Set port early
        System.setProperty("server.port", String.valueOf(config.getServerPort()));

        SpringApplication.run(ProjectApplication.class, args);
    }
}
