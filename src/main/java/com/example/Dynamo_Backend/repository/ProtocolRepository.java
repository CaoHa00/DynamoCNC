package com.example.Dynamo_Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Dynamo_Backend.entities.Protocol;

public interface ProtocolRepository extends JpaRepository<Protocol, String> {

}
