package com.example.Dynamo_Backend.service.implementation;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Dynamo_Backend.dto.MachineKpiDto;
import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.entities.MachineKpi;
import com.example.Dynamo_Backend.entities.Staff;
import com.example.Dynamo_Backend.mapper.MachineKpiMapper;
import com.example.Dynamo_Backend.repository.MachineKpiRepository;
import com.example.Dynamo_Backend.repository.MachineRepository;
import com.example.Dynamo_Backend.service.MachineKpiService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MachineKpiImplementation implements MachineKpiService {
    MachineKpiRepository machineKpiRepository;
    MachineRepository machineRepository;

    @Override
    public MachineKpiDto addMachineKpi(MachineKpiDto machineKpiDto) {
        long createdTimestamp = System.currentTimeMillis();
        Machine machine = machineRepository.findById(machineKpiDto.getMachineId())
                .orElseThrow(() -> new RuntimeException("Machine is not found:" + machineKpiDto.getMachineId()));
        MachineKpi machineKpi = MachineKpiMapper.mapToMachineKpi(machineKpiDto);
        machineKpi.setMachine(machine);
        machineKpi.setCreatedDate(createdTimestamp);
        machineKpi.setUpdatedDate(createdTimestamp);
        MachineKpi saveMachineKpi = machineKpiRepository.save(machineKpi);
        return MachineKpiMapper.mapToMachineKpiDto(saveMachineKpi);
    }

    @Override
    public MachineKpiDto updateMachineKpi(Integer Id, MachineKpiDto machineKpiDto) {
        MachineKpi machineKpi = machineKpiRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Machine is not found:" + Id));
        long updatedTimestamp = System.currentTimeMillis();
        Machine machine = machineRepository.findById(machineKpiDto.getMachineId())
                .orElseThrow(() -> new RuntimeException("Machine is not found:" + machineKpiDto.getMachineId()));
        machineKpi.setMachine(machine);
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
    public MachineKpiDto updateMachineKpiByMachineId(Integer machineId, MachineKpiDto machineKpiDto) {
        long updatedTimestamp = System.currentTimeMillis();
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("MM"));
        MachineKpi machineKpi = machineKpiRepository.findByMachine_machineId(machineId).stream()
                .filter(kpi -> currentMonth.equals(String.format("%02d", kpi.getMonth())))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "No machineKpi found for machine ID: " + machineId));

        Machine machine = machineRepository.findById(machineId)
                .orElseThrow(() -> new RuntimeException("MachineKpi is not found:" + machineKpiDto.getMachineId()));
        machineKpi.setMachine(machine);
        machineKpi.setYear(machineKpiDto.getYear());
        machineKpi.setMonth(machineKpiDto.getMonth());
        machineKpi.setMachineMiningTarget(machineKpiDto.getMachineMiningTarget());
        machineKpi.setOee(machineKpiDto.getOee());
        machineKpi.setUpdatedDate(updatedTimestamp);
        MachineKpi saveMachineKpi = machineKpiRepository.save(machineKpi);
        return MachineKpiMapper.mapToMachineKpiDto(saveMachineKpi);
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
                machineKpiDto.setMachineMiningTarget((int) row.getCell(4).getNumericCellValue());
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
