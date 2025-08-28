package com.example.Dynamo_Backend.service;

public interface PasswordResetService {
    void initiatePasswordReset(String email);

    void resetPassword(String token, String newPassword);

    boolean validateResetToken(String token);

    String generateSecureToken();

    void cleanupExpiredTokens();
}
