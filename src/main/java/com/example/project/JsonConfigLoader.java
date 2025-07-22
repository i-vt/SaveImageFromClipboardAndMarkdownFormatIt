package com.example.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.File;

@Configuration
public class JsonConfigLoader {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private Environment env;

    @PostConstruct
    public void loadJsonConfig() throws Exception {
        String configPath = env.getProperty("config.path");
        if (configPath == null) {
            throw new IllegalStateException("Missing --config.path argument");
        }

        File configFile = new File(configPath);
        if (!configFile.exists()) {
            throw new IllegalStateException("Config file not found: " + configPath);
        }

        ObjectMapper mapper = new ObjectMapper();
        AppConfig loadedConfig = mapper.readValue(configFile, AppConfig.class);

        appConfig.setUploadDir(loadedConfig.getUploadDir());
        appConfig.setImageBasePath(loadedConfig.getImageBasePath());
        appConfig.setImageEndpointPrefix(loadedConfig.getImageEndpointPrefix());
        appConfig.setServerPort(loadedConfig.getServerPort());

        System.setProperty("server.port", String.valueOf(appConfig.getServerPort()));
    }

}
