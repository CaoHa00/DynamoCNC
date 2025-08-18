package com.example.Dynamo_Backend.service.implementation;

import java.io.InputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Dynamo_Backend.dto.MachineKpiDto;
import com.example.Dynamo_Backend.entities.Group;

import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.entities.MachineKpi;
import com.example.Dynamo_Backend.mapper.MachineKpiMapper;
import com.example.Dynamo_Backend.repository.GroupRepository;
import com.example.Dynamo_Backend.repository.MachineKpiRepository;
import com.example.Dynamo_Backend.repository.MachineRepository;
import com.example.Dynamo_Backend.service.MachineKpiService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MachineKpiImplementation implements MachineKpiService {
    MachineKpiRepository machineKpiRepository;
    MachineRepository machineRepository;
    GroupRepository groupRepository;

    @Override
    public MachineKpiDto addMachineKpi(MachineKpiDto machineKpiDto) {
        long createdTimestamp = System.currentTimeMillis();
        MachineKpi machineKpi = machineKpiRepository.findByMachine_machineIdAndMonthAndYear(machineKpiDto.getId(),
                machineKpiDto.getMonth(), machineKpiDto.getYear());
        if (machineKpi != null) {
            throw new IllegalArgumentException("Goal of this machine is already set");
        }
        Machine machine = machineRepository.findById(machineKpiDto.getMachineId())
                .orElseThrow(() -> new RuntimeException("Machine is not found:" + machineKpiDto.getMachineId()));
        machineKpi = MachineKpiMapper.mapToMachineKpi(machineKpiDto);
        Group group = groupRepository.findById(machineKpiDto.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group is not found:" + machineKpiDto.getGroupId()));
        machineKpi.setGroup(group);
        machineKpi.setMachine(machine);
        machineKpi.setCreatedDate(createdTimestamp);
        machineKpi.setUpdatedDate(createdTimestamp);
        MachineKpi saveMachineKpi = machineKpiRepository.save(machineKpi);
        return MachineKpiMapper.mapToMachineKpiDto(saveMachineKpi);
    }

    @Override
    public MachineKpiDto updateMachineKpi(Integer Id, MachineKpiDto machineKpiDto) {
        MachineKpi machineKpi = machineKpiRepository.findByMachine_machineIdAndMonthAndYear(
                machineKpiDto.getMachineId(),
                machineKpiDto.getMonth(), machineKpiDto.getYear());
        if (machineKpi != null && !machineKpi.getId().equals(Id)) {
            throw new IllegalArgumentException("Goal of this machine is already set");
        }
        if (machineKpi != null && machineKpi.isSameAs(machineKpiDto)) {
            throw new IllegalArgumentException("Goal of this machine is already set");
        }

        if (machineKpi == null) {
            machineKpi = machineKpiRepository.findById(Id)
                    .orElseThrow(() -> new RuntimeException("StaffKpi not found with id: " + Id));
        }
        Machine machine = machineRepository.findById(machineKpiDto.getMachineId()).orElse(null);
        long updatedTimestamp = System.currentTimeMillis();
        Group group = groupRepository.findById(machineKpiDto.getGroupId()).orElse(null);
        machineKpi.setMachine(machine);
        machineKpi.setGroup(group);
        machineKpi.setYear(machineKpiDto.getYear());
        machineKpi.setMonth(machineKpiDto.getMonth());
        machineKpi.setMachineMiningTarget(machineKpiDto.getMachineMiningTarget());
        machineKpi.setOee(machineKpiDto.getOee());
        machineKpi.setUpdatedDate(updatedTimestamp);
        MachineKpi saveMachineKpi = machineKpiRepository.save(machineKpi);
        return MachineKpiMapper.mapToMachineKpiDto(saveMachineKpi);
    }

    @Override
    public MachineKpiDto getMachineKpiById(Integer Id) {
        MachineKpi machineKpi = machineKpiRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Machine is not found:" + Id));
        return MachineKpiMapper.mapToMachineKpiDto(machineKpi);
    }

    @Override
    public void deleteMachineKpi(Integer Id) {
        MachineKpi machineKpi = machineKpiRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Machine is not found:" + Id));
        machineKpiRepository.delete(machineKpi);
    }

    @Override
    public List<MachineKpiDto> getMachineKpis() {
        List<MachineKpi> machineKpis = machineKpiRepository.findAll();
        return machineKpis.stream().map(MachineKpiMapper::mapToMachineKpiDto).toList();
    }

    @Override
    public MachineKpiDto updateMachineKpiByMachineId(Integer Id, MachineKpiDto machineKpiDto) {
        long updatedTimestamp = System.currentTimeMillis();

        MachineKpi machineKpi = machineKpiRepository.findByMachine_machineIdAndMonthAndYear(Id,
                machineKpiDto.getMonth(), machineKpiDto.getYear());
        if (machineKpi.isSameAs(machineKpiDto)) {
            throw new IllegalArgumentException("Goal of this machine is already set");
        } else {
            Group group = groupRepository.findById(machineKpiDto.getGroupId()).orElse(null);
            machineKpi.setGroup(group);
            machineKpi.setYear(machineKpiDto.getYear());
            machineKpi.setMonth(machineKpiDto.getMonth());
            machineKpi.setMachineMiningTarget(machineKpiDto.getMachineMiningTarget());
            machineKpi.setOee(machineKpiDto.getOee());
            if (machineKpiDto != null) {
                machineKpi.setCreatedDate(machineKpi.getCreatedDate());
            }
            machineKpi.setUpdatedDate(updatedTimestamp);
            MachineKpi saveMachineKpi = machineKpiRepository.save(machineKpi);
            return MachineKpiMapper.mapToMachineKpiDto(saveMachineKpi);
        }
    }

    @Override
    public void importMachineKpiFromExcel(MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0)
                    continue; // Skip header row
                MachineKpiDto machineKpiDto = new MachineKpiDto();
                machineKpiDto.setYear((int) row.getCell(0).getNumericCellValue());
                machineKpiDto.setMonth((int) row.getCell(1).getNumericCellValue());
                String idCell = row.getCell(0).getStringCellValue();
                String machineId = idCell.substring(idCell.length() - 3, idCell.length() - 1);
                machineKpiDto.setMachineId(Integer.parseInt(machineId));
                machineKpiDto.setMachineMiningTarget((float) row.getCell(4).getNumericCellValue());
                machineKpiDto.setOee((float) row.getCell(5).getNumericCellValue());
                addMachineKpi(machineKpiDto);
            }
            workbook.close();
            inputStream.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to import machine KPIs from Excel file", e);
        }
    }

}
