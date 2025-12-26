package com.example.Dynamo_Backend.service;

import com.example.Dynamo_Backend.entities.MachineSegment;

public interface MachineSegmentService {
    void addNewSegmet(String payload, String previousStatus, Long previousTime, Long currentTime);

    MachineSegment updateSegemnt();

}
