package com.example.Dynamo_Backend.service.implementation;

import com.example.Dynamo_Backend.entities.CurrentStatus;
import com.example.Dynamo_Backend.entities.MachineSegment;
import com.example.Dynamo_Backend.repository.CurrentStatusRepository;
import com.example.Dynamo_Backend.service.MachineSegmentService;

public class MachineSegmentImplementation implements MachineSegmentService {

    @Override
    public MachineSegment updateSegemnt() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateSegemnt'");
    }

    @Override
    public void addNewSegmet(String payload, String previousStatus, Long previousTime) {
        String[] arr = payload.split("-");
        String machineId = arr[0];
        int machineIdInt = Integer.parseInt(machineId) + 1;
        MachineSegment machineSegement = new MachineSegment();
        machineSegement.setStatus(previousStatus);

    }

}
