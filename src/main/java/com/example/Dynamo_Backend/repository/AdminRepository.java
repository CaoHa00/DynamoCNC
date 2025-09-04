package com.example.Dynamo_Backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Dynamo_Backend.entities.Admin;

public interface AdminRepository extends JpaRepository<Admin, String> {
    Optional<Admin> findByEmail(String email);

    Optional<Admin> findByUsername(String username);

    boolean existsByEmail(String email);
}
