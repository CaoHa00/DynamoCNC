package com.example.Dynamo_Backend.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Dynamo_Backend.entities.Admin;
import com.example.Dynamo_Backend.entities.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {
    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByAdminAndUsedFalse(Admin admin);

    void deleteByAdmin(Admin admin);

    void deleteByExpiryDateBefore(LocalDateTime date);
}
