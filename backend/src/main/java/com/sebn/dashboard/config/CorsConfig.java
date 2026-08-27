package com.sebn.dashboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS configuration allowing the React Vite frontend during local development.
 */
@Configuration
public class CorsConfig {

    private static final String[] FRONTEND_ORIGINS = {
        "http://localhost:5173",
        "http://localhost:8081",
        "http://localhost:8082",
        // Docker Compose demo: frontend container exposed on port 3000
        "http://localhost:3000"
    };

    /**
     * Registers CORS mappings for all API endpoints.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins(FRONTEND_ORIGINS)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
}
