package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.entities.CurrentStatus;

public interface CurrentStatusService {
    void addCurrentStatus(String payload);

    List<CurrentStatus> all();
}
