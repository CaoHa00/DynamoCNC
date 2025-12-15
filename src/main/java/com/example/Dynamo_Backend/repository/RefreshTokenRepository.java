package com.example.Dynamo_Backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Dynamo_Backend.entities.Admin;
import com.example.Dynamo_Backend.entities.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByAdmin(Admin admin);
}
