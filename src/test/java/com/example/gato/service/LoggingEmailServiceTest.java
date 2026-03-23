package com.example.gato.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class LoggingEmailServiceTest {

    private final LoggingEmailService service = new LoggingEmailService();

    @Test
    void sendLogsEmailDetailsWhenSmtpIsDisabled(CapturedOutput output) {
        service.send("user@example.com", "Hello", "<p>Body</p>", true);

        assertThat(output.getOut())
                .contains("[EMAIL DISABLED]")
                .contains("user@example.com")
                .contains("Hello")
                .contains("<p>Body</p>");
    }
}
