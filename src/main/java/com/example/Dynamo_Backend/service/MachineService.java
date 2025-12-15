package com.example.Dynamo_Backend.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.Dynamo_Backend.dto.MachineDto;
import com.example.Dynamo_Backend.dto.RequestDto.MachineRequestDto;

public interface MachineService {

    MachineDto addMachine(MachineRequestDto machineDto);

    MachineDto updateMachine(Integer Id, MachineRequestDto machineDto);

    MachineDto getMachineById(Integer Id);

    void deleteMachine(Integer Id);

    List<MachineDto> getMachines();

    List<MachineDto> getActiveMachines();

    void importMachineFromExcel(MultipartFile file);
}
