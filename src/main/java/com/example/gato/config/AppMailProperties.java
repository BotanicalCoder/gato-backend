package com.example.gato.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.mail")
public record AppMailProperties(
        String fromAddress,
        String fromName
) {
}
