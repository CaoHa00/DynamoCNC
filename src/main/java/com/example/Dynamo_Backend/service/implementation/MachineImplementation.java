package com.example.Dynamo_Backend.service.implementation;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Dynamo_Backend.dto.MachineDto;
import com.example.Dynamo_Backend.dto.MachineKpiDto;
import com.example.Dynamo_Backend.dto.RequestDto.MachineRequestDto;
import com.example.Dynamo_Backend.entities.Group;
import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.entities.MachineKpi;
import com.example.Dynamo_Backend.mapper.MachineKpiMapper;
import com.example.Dynamo_Backend.mapper.MachineMapper;
import com.example.Dynamo_Backend.repository.GroupRepository;
import com.example.Dynamo_Backend.repository.MachineKpiRepository;
import com.example.Dynamo_Backend.repository.MachineRepository;
import com.example.Dynamo_Backend.service.MachineKpiService;
import com.example.Dynamo_Backend.service.MachineService;

import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Service
@AllArgsConstructor
public class MachineImplementation implements MachineService {
    @Autowired
    MachineRepository machineRepository;
    @Autowired
    GroupRepository groupRepository;

    MachineKpiRepository machineKpiRepository;
    MachineKpiService machineKpiService;

    @Override
    public MachineDto addMachine(MachineRequestDto machineDto) {
        int status = 0;
        long createdTimestamp = System.currentTimeMillis();
        Machine machine = MachineMapper.mapToMachine(machineDto);
        machine.setStatus(status);
        machine.setCreatedDate(createdTimestamp);
        machine.setUpdatedDate(createdTimestamp);
        machine.setMachineKpis(new ArrayList<MachineKpi>());
        Group group = groupRepository.findById(machineDto.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group is not found:" + machineDto.getGroupId()));

        machine.setGroup(group);
        Machine saveMachine = machineRepository.save(machine);

        machineDto.setMachineId(saveMachine.getMachineId());
        MachineKpiDto machineKpiDto = MachineKpiMapper.mapToMachineKpiDto(machineDto);
        machineKpiService.addMachineKpi(machineKpiDto);
        // saveMachine.setMachineKpis(new ArrayList<>());
        // saveMachine.getMachineKpis().add(MachineKpiMapper.mapToMachineKpi(saveMachineKpiDto));

        MachineDto result = MachineMapper.mapToMachineDto(saveMachine);

        // return MachineMapper.mapToMachineDto(saveMachine);
        return result;

    }

    @Override
    public MachineDto updateMachine(Integer Id, MachineRequestDto machineDto) {
        Machine machine = machineRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Machine is not found:" + Id));
        long updatedTimestamp = System.currentTimeMillis();
        machine.setMachineGroup(machineDto.getMachineGroup());
        machine.setMachineName(machineDto.getMachineName());
        machine.setMachineOffice(machineDto.getMachineOffice());
        machine.setMachineType(machineDto.getMachineType());
        machine.setStatus(machineDto.getStatus());
        machine.setUpdatedDate(updatedTimestamp);
        Group group = groupRepository.findById(machineDto.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group is not found:" + machineDto.getGroupId()));
        machine.setGroup(group);
        MachineKpiDto machineKpiDto = MachineKpiMapper.mapToMachineKpiDto(machineDto);
        machineKpiService.updateMachineKpiByMachineId(Id, machineKpiDto);
        Machine updatedMachine = machineRepository.save(machine);
        return MachineMapper.mapToMachineDto(updatedMachine);
    }

    @Override
    public MachineDto getMachineById(Integer Id) {
        Machine machine = machineRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Machine is not found:" + Id));
        return MachineMapper.mapToMachineDto(machine);
    }

    @Override
    public void deleteMachine(Integer Id) {
        Machine machine = machineRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Machine is not found:" + Id));
        machineRepository.delete(machine);
        List<MachineKpi> machineKpis = machineKpiRepository.findByMachine_machineId(Id);
        for (MachineKpi machineKpi : machineKpis) {
            machineKpiRepository.delete(machineKpi);
        }
    }

    @Override
    public List<MachineDto> getMachines() {
        List<Machine> machines = machineRepository.findAll();
        return machines.stream().map(MachineMapper::mapToMachineDto).toList();

    }

    @Override
    public void importMachineFromExcel(MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            List<Machine> machineList = new ArrayList<>();

            for (Row row : sheet) {
                if (row.getRowNum() == 0)
                    continue;
                Machine machineDto = new Machine();
                String idCell = row.getCell(0).getStringCellValue();
                String machineId = idCell.substring(idCell.length() - 3, idCell.length() - 1);
                machineDto.setMachineId(Integer.parseInt(machineId));
                machineDto.setMachineName(row.getCell(1).getStringCellValue());
                Group group = groupRepository.findByGroupName(row.getCell(2).getStringCellValue())
                        .orElseThrow(() -> new RuntimeException(
                                "Group is not found when add machine by excel:" + row.getCell(2).getStringCellValue()));
                machineDto.setGroup(group);
                machineDto.setMachineType(row.getCell(3).getStringCellValue());
                machineDto.setMachineGroup(row.getCell(4).getStringCellValue());
                machineDto.setMachineOffice(row.getCell(5).getStringCellValue());

                machineDto.setStatus(1);

                machineList.add(machineDto);
            }
            machineRepository.saveAll(machineList);
            workbook.close();
            inputStream.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to import machines from Excel file: " + e.getMessage());
        }
    }

    @Override
    public List<MachineDto> getActiveMachines() {
        List<Machine> machines = machineRepository.findAllByStatus(1);
        return machines.stream().map(MachineMapper::mapToMachineDto).toList();
    }

}
