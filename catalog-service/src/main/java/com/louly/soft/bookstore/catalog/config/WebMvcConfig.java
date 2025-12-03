package com.louly.soft.bookstore.catalog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedMethods("*") // allowing all methods [GET, POST, PUT etc..]
                .allowedHeaders("*") // allowing all headers
                .allowedOriginPatterns("*") // allowing all domains like localhost
                .allowCredentials(false); // want to share cookies or not
    }
}
