package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.MachineDto;

public interface MachineService {

    MachineDto addMachine(MachineDto machineDto);

    MachineDto updateMachine(Integer Id, MachineDto machineDto);

    MachineDto getMachineById(Integer Id);

    void deleteMachine(Integer Id);

    List<MachineDto> getMachines();
}
