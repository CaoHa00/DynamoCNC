package com.example.Dynamo_Backend.service;

public interface EmailService {
    void sendPasswordResetEmail(String toEmail, String resetToken);

    String buildPasswordResetEmailTemplate(String resetUrl);
}
