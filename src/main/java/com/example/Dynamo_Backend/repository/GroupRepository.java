package com.example.Dynamo_Backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Dynamo_Backend.entities.Group;

public interface GroupRepository extends JpaRepository<Group, String> {
    List<Group> findByGroupType(String groupType);
}
