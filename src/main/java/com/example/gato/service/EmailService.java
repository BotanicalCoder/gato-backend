package com.example.gato.service;

public interface EmailService {
    void send(String to, String subject, String body, boolean html);
}
