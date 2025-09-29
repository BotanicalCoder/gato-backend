package com.example.gato.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MockEmailService implements EmailService {
    @Override
    public void send(String to, String subject, String body) {
        log.info("[MOCK-EMAIL] To: {} | Subject: {}\n{}", to, subject, body);
    }
}
