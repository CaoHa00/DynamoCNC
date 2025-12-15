package com.example.Dynamo_Backend.service.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.exception.BusinessException;
import com.example.Dynamo_Backend.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImplementation implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.frontend.url:http://localhost:3002}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Password Reset Request - Dynamo CNC");

            String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;

            String htmlContent = buildPasswordResetEmailTemplate(resetUrl);
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new BusinessException("Failed to send password reset email: " + e.getMessage());
        }
    }

    @Override
    public String buildPasswordResetEmailTemplate(String resetUrl) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Password Reset</title>
                    <style>
                        body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4; }
                        .container { max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                        .header { text-align: center; margin-bottom: 30px; }
                        .logo { font-size: 24px; font-weight: bold; color: #2c3e50; }
                        .content { line-height: 1.6; color: #333; }
                        .reset-button { display: inline-block; background-color: #3498db; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                        .footer { margin-top: 30px; padding-top: 20px; border-top: 1px solid #eee; font-size: 12px; color: #666; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <div class="logo">Dynamo CNC</div>
                        </div>

                        <div class="content">
                            <h2>Password Reset Request</h2>

                            <p>Hello,</p>

                            <p>We received a request to reset your password for your Dynamo CNC account.</p>

                            <p>Click the button below to reset your password:</p>

                            <p style="text-align: center;">
                                <a href="%s" class="reset-button">Reset My Password</a>
                            </p>

                            <p>Or copy and paste this link into your browser:</p>
                            <p style="word-break: break-all; background-color: #f8f9fa; padding: 10px; border-radius: 4px;">%s</p>

                            <p><strong>This link will expire in 24 hours.</strong></p>

                            <p>If you did not request this password reset, please ignore this email. Your password will remain unchanged.</p>

                            <p>Best regards,<br>Dynamo CNC Team</p>
                        </div>

                        <div class="footer">
                            <p>This is an automated message. Please do not reply to this email.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(resetUrl, resetUrl);
    }
}
