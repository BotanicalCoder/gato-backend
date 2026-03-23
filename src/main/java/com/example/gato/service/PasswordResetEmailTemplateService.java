package com.example.gato.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class PasswordResetEmailTemplateService {

    private static final String TEMPLATE_LOCATION = "classpath:templates/email/password-reset.html";

    private final ResourceLoader resourceLoader;

    public PasswordResetEmailTemplateService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public String render(String displayName, String token, long tokenTtlMinutes) {
        String safeDisplayName = escapeHtml(displayName == null || displayName.isBlank() ? "there" : displayName);
        String safeToken = escapeHtml(token);
        String safeTtl = Long.toString(tokenTtlMinutes);

        return applyTemplate(loadTemplate(), Map.of(
                "displayName", safeDisplayName,
                "token", safeToken,
                "tokenTtlMinutes", safeTtl
        ));
    }

    private String loadTemplate() {
        Resource resource = resourceLoader.getResource(TEMPLATE_LOCATION);
        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load password reset email template", ex);
        }
    }

    private String applyTemplate(String template, Map<String, String> variables) {
        String rendered = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            rendered = rendered.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return rendered;
    }

    private String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
