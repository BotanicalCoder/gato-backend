package com.example.gato.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnMissingBean(EmailService.class)
public class LoggingEmailService implements EmailService {

    @Override
    public void send(String to, String subject, String body, boolean html) {
        log.info("[EMAIL DISABLED] To: {} | Subject: {} | Html: {}\n{}", to, subject, html, body);
    }
}
