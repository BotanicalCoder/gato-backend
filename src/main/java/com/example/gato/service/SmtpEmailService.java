package com.example.gato.service;

import com.example.gato.config.AppMailProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@ConditionalOnBean(JavaMailSender.class)
public class SmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;
    private final AppMailProperties mailProperties;

    @Override
    public void send(String to, String subject, String body, boolean html) {
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("Recipient email address cannot be null or empty");
        }
        if (subject == null) {
            throw new IllegalArgumentException("Email subject cannot be null");
        }
        if (body == null) {
            throw new IllegalArgumentException("Email body cannot be null");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, html);
            helper.setFrom(buildFromAddress());
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException | MailException ex) {
            throw new IllegalStateException("Failed to send email", ex);
        }
    }

    @NonNull
    private InternetAddress buildFromAddress() throws UnsupportedEncodingException, AddressException {
        String fromAddress = mailProperties.fromAddress();
        String fromName = mailProperties.fromName();
        return fromName == null || fromName.isBlank()
                ? new InternetAddress(fromAddress)
                : new InternetAddress(fromAddress, fromName);
    }
}
