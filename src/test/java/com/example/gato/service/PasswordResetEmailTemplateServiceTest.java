package com.example.gato.service;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PasswordResetEmailTemplateServiceTest {

    @Test
    void renderReplacesPlaceholdersAndEscapesHtml() {
        PasswordResetEmailTemplateService service =
                new PasswordResetEmailTemplateService(new DefaultResourceLoader());

        String result = service.render("<Admin>", "abc<123>&", 30);

        assertThat(result).contains("Hi &lt;Admin&gt;,");
        assertThat(result).contains("abc&lt;123&gt;&amp;");
        assertThat(result).contains("within 30 minutes");
        assertThat(result).doesNotContain("{{displayName}}");
        assertThat(result).doesNotContain("{{token}}");
    }

    @Test
    void renderFallsBackToThereWhenDisplayNameIsBlank() {
        PasswordResetEmailTemplateService service =
                new PasswordResetEmailTemplateService(new DefaultResourceLoader());

        String result = service.render("   ", "token-123", 15);

        assertThat(result).contains("Hi there,");
    }

    @Test
    void renderWrapsTemplateLoadingFailures() {
        ResourceLoader failingLoader = new ResourceLoader() {
            @Override
            public org.springframework.core.io.Resource getResource(String location) {
                return new ByteArrayResource(new byte[0]) {
                    @Override
                    public InputStream getInputStream() throws IOException {
                        throw new IOException("boom");
                    }
                };
            }

            @Override
            public ClassLoader getClassLoader() {
                return getClass().getClassLoader();
            }
        };
        PasswordResetEmailTemplateService service = new PasswordResetEmailTemplateService(failingLoader);

        assertThatThrownBy(() -> service.render("User", "token", 30))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Failed to load password reset email template");
    }
}
