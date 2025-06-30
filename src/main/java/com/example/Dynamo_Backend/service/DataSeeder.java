package com.example.Dynamo_Backend.service;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.Dynamo_Backend.entities.Group;
import com.example.Dynamo_Backend.repository.GroupRepository;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seedDefaults(
            GroupRepository groupRepository) {
        return args -> {
            if (groupRepository.count() == 0) {
                groupRepository.saveAll(List.of(
                        new Group(null, "Group 1", null, null),
                        new Group(null, "Group 2", null, null),
                        new Group(null, "Group 3", null, null),
                        new Group(null, "Group 4", null, null),
                        new Group(null, "Group 5", null, null),
                        new Group(null, "Group 6", null, null),
                        new Group(null, "Group 7", null, null),
                        new Group(null, "Group 8", null, null),
                        new Group(null, "Group 9", null, null),
                        new Group(null, "Group 10", null, null)));
            }
        };
    }
}
