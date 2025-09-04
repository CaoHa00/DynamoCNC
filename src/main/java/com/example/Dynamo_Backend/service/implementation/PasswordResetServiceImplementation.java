package com.example.Dynamo_Backend.service.implementation;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Dynamo_Backend.entities.Admin;
import com.example.Dynamo_Backend.entities.PasswordResetToken;
import com.example.Dynamo_Backend.exception.*;
import com.example.Dynamo_Backend.repository.AdminRepository;
import com.example.Dynamo_Backend.repository.PasswordResetTokenRepository;
import com.example.Dynamo_Backend.security.JwtUtil;
import com.example.Dynamo_Backend.service.EmailService;

@Service
@Transactional
public class PasswordResetServiceImplementation {
    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    private static final SecureRandom random = new SecureRandom();

    public void initiatePasswordReset(String email) {
        Admin account = adminRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Account"));

        String resetToken = generateSecureToken();

        tokenRepository.deleteByAdmin(account);

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setAdmin(account);
        passwordResetToken.setToken(resetToken);
        tokenRepository.save(passwordResetToken);

        emailService.sendPasswordResetEmail(email, resetToken);
    }

    public void resetPassword(String token, String newPassword, String method, String confirmPassword,
            String currentPassword) {
        if ("update".equals(method)) {
            if (!jwtUtil.validateToken(token)) {
                throw new UnauthorizedException("token");
            }
            String tokenAccountId = jwtUtil.getAccountIdFromToken(token);
            Admin account = adminRepository.findById(tokenAccountId)
                    .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

            if (currentPassword == null) {
                throw new Error("passwordIncorrect");
            }

            if (!currentPassword.equals(account.getPassword())) {
                throw new Error("passwordIncorrect");
            }

            if (!newPassword.equals(confirmPassword)) {
                throw new Error("mismatch");
            }

            account.setPassword(newPassword);
            adminRepository.save(account);

        } else if ("reset".equals(method)) {
            PasswordResetToken resetToken = tokenRepository.findByToken(token)
                    .orElseThrow(() -> new InvalidTokenException("token"));

            if (resetToken.isUsed()) {
                throw new InvalidTokenException("used");
            }

            if (resetToken.isExpired()) {
                throw new InvalidTokenException("expired");
            }

            Admin admin = resetToken.getAdmin();

            admin.setPassword(passwordEncoder.encode(newPassword));
            adminRepository.save(admin);

            resetToken.setUsed(true);
            tokenRepository.save(resetToken);
        } else {
            throw new ResourceNotFoundException("method should be 'reset' or 'update'. method was: " + method);
        }
    }

    public boolean validateResetToken(String token) {
        Optional<PasswordResetToken> resetToken = tokenRepository.findByToken(token);

        if (resetToken.isEmpty()) {
            return false;
        }

        PasswordResetToken tokenEntity = resetToken.get();
        return !tokenEntity.isUsed() && !tokenEntity.isExpired();
    }

    private String generateSecureToken() {
        byte[] tokenBytes = new byte[32];
        random.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    @Scheduled(cron = "0 0 2 * * ?") // Run daily at 2 AM
    public void cleanupExpiredTokens() {
        tokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }
}
