package com.example.gato.service;

import com.example.gato.config.AppMailProperties;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SmtpEmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    private final AppMailProperties mailProperties =
            new AppMailProperties("noreply@gato.dev", "Gato Team");

    @InjectMocks
    private SmtpEmailService service;

    @Test
    void sendBuildsAndSendsHtmlMessage() throws Exception {
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        service = new SmtpEmailService(mailSender, mailProperties);
        service.send("user@example.com", "Reset your password", "<p>Hello</p>", true);

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(captor.capture());
        MimeMessage sent = captor.getValue();
        assertThat(sent.getAllRecipients()[0].toString()).isEqualTo("user@example.com");
        assertThat(sent.getSubject()).isEqualTo("Reset your password");
        assertThat(sent.getFrom()[0].toString()).isEqualTo("Gato Team <noreply@gato.dev>");
        assertThat(sent.getContent().toString()).contains("<p>Hello</p>");
    }

    @Test
    void sendRejectsBlankRecipient() {
        service = new SmtpEmailService(mailSender, mailProperties);

        assertThatThrownBy(() -> service.send("   ", "Subject", "Body", false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Recipient email address cannot be null or empty");
    }

    @Test
    void sendWrapsMailSenderFailures() {
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MailSendException("smtp down")).when(mailSender).send(any(MimeMessage.class));

        service = new SmtpEmailService(mailSender, mailProperties);

        assertThatThrownBy(() -> service.send("user@example.com", "Subject", "Body", false))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Failed to send email");
    }
}
