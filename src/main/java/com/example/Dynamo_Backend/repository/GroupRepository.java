package com.example.Dynamo_Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Dynamo_Backend.entities.Group;

public interface GroupRepository extends JpaRepository<Group, String> {

}
