package com.example.CMCmp3.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir; // = "./uploads"

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Convert "./uploads" -> absolute path
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        String uploadAbsolutePath = uploadPath.toFile().getAbsolutePath();

        System.out.println("Serving images from: " + uploadAbsolutePath);

        // To folder: ./uploads/<filename>
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadAbsolutePath + "/");
    }
}
