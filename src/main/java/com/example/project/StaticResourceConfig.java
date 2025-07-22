package com.example.project;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    private final AppConfig config;

    public StaticResourceConfig(AppConfig config) {
        this.config = config;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String webPath = config.getImageBasePath();
        if (!webPath.endsWith("/")) webPath += "/";

        String uploadPath = config.getUploadDir();
        if (!uploadPath.endsWith("/")) uploadPath += "/";

        registry.addResourceHandler(webPath + "**")
                .addResourceLocations("file:" + uploadPath);
    }
}
