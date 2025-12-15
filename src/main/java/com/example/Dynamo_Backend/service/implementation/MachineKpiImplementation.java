package com.example.Dynamo_Backend.service.implementation;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Dynamo_Backend.dto.MachineKpiDto;
import com.example.Dynamo_Backend.entities.Group;

import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.entities.MachineKpi;
import com.example.Dynamo_Backend.exception.BusinessException;
import com.example.Dynamo_Backend.exception.ResourceNotFoundException;
import com.example.Dynamo_Backend.mapper.MachineKpiMapper;
import com.example.Dynamo_Backend.repository.GroupRepository;
import com.example.Dynamo_Backend.repository.MachineKpiRepository;
import com.example.Dynamo_Backend.repository.MachineRepository;
import com.example.Dynamo_Backend.service.MachineKpiService;

import jakarta.transaction.Transactional;
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
        MachineKpi machineKpi = machineKpiRepository.findByMachine_machineIdAndMonthAndYear(
                machineKpiDto.getMachineId(),
                machineKpiDto.getMonth(), machineKpiDto.getYear());
        if (machineKpi != null) {
            throw new BusinessException("Goal of this machine is already set");
        }
        Machine machine = machineRepository.findById(machineKpiDto.getMachineId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Machine is not found:" + machineKpiDto.getMachineId()));
        machineKpi = MachineKpiMapper.mapToMachineKpi(machineKpiDto);
        Group group = groupRepository.findById(machineKpiDto.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Group is not found:" + machineKpiDto.getGroupId()));
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
            throw new BusinessException("Goal of this machine is already set");
        }
        if (machineKpi != null && machineKpi.isSameAs(machineKpiDto)) {
            throw new BusinessException("Goal of this machine is already set");
        }

        if (machineKpi == null) {
            machineKpi = machineKpiRepository.findById(Id)
                    .orElseThrow(() -> new ResourceNotFoundException("StaffKpi not found with id: " + Id));
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
                .orElseThrow(() -> new ResourceNotFoundException("Machine is not found:" + Id));
        return MachineKpiMapper.mapToMachineKpiDto(machineKpi);
    }

    @Override
    public void deleteMachineKpi(Integer Id) {
        MachineKpi machineKpi = machineKpiRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("Machine is not found:" + Id));
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
            throw new BusinessException("Goal of this machine is already set");
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
            List<MachineKpi> machineKpis = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() < 6)
                    continue; // Skip header row
                boolean missing = false;
                for (int i = 2; i <= 7; i++) {
                    if (row.getCell(i) == null) {
                        missing = true;
                        break;
                    }
                }
                if (missing)
                    continue;
                MachineKpi machineKpi = new MachineKpi();
                machineKpi.setYear((int) row.getCell(2).getNumericCellValue());
                machineKpi.setMonth((int) row.getCell(3).getNumericCellValue());
                Cell idCell = row.getCell(4);
                String machineIdStr;

                if (idCell.getCellType() == CellType.NUMERIC) {
                    machineIdStr = String.valueOf((int) idCell.getNumericCellValue());
                } else {
                    machineIdStr = idCell.getStringCellValue().replaceAll("\\D+", "");
                }
                if (machineIdStr != null && !machineIdStr.isEmpty()) {
                    machineKpi.setMachine(machineRepository.findById(Integer.parseInt(machineIdStr))
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Machine not found when import file excel with id: " + machineIdStr)));
                } else {
                    continue;
                }
                String groupCell = row.getCell(5).getStringCellValue();
                Group group = groupRepository.findByGroupName(groupCell)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Group not found when import file excel with id: " + groupCell));
                machineKpi.setGroup(group);
                machineKpi.setMachineMiningTarget((float) row.getCell(6).getNumericCellValue());
                machineKpi.setOee((float) row.getCell(7).getNumericCellValue());
                long createdTimestamp = System.currentTimeMillis();
                machineKpi.setCreatedDate(createdTimestamp);
                machineKpi.setUpdatedDate(createdTimestamp);
                machineKpis.add(machineKpi);
            }
            machineKpiRepository.saveAll(machineKpis);
            workbook.close();
            inputStream.close();
        } catch (Exception e) {
            throw new BusinessException("Failed to import machine KPIs from Excel file: " + e.getMessage());
        }
    }

    @Transactional
    @Scheduled(cron = "0 0 0 1 * ?") // Runs at 12:00 AM on the 1st of every month
    public void createMonthlyMachineKpis() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        // Previous month
        int prevYear = (month == 1) ? year - 1 : year;
        int prevMonth = (month == 1) ? 12 : month - 1;

        List<Machine> activeMachines = machineRepository.findAll();
        int createdCount = 0;

        for (Machine machine : activeMachines) {
            // Check if KPI already exists for current month
            MachineKpi existing = machineKpiRepository.findByMachine_machineIdAndMonthAndYear(machine.getMachineId(),
                    month, year);
            if (existing != null) {
                continue; // Skip if already exists
            }

            // Fetch previous month's KPI
            MachineKpi prevKpi = machineKpiRepository.findByMachine_machineIdAndMonthAndYear(machine.getMachineId(),
                    prevMonth, prevYear);

            MachineKpiDto dto = new MachineKpiDto();
            dto.setMachineId(machine.getMachineId());
            dto.setYear(year);
            dto.setMonth(month);

            if (prevKpi != null) {
                dto.setOee(prevKpi.getOee());
                dto.setMachineMiningTarget(prevKpi.getMachineMiningTarget());
                dto.setGroupId(prevKpi.getGroup().getGroupId());
            } else {
                List<Group> group = groupRepository.findAll();
                dto.setOee(0.0f);
                dto.setMachineMiningTarget(0.0f);
                dto.setGroupId(group.get(0).getGroupId());
            }

            // Get group from machine's groups (assume first one)
            try {
                addMachineKpi(dto);
                createdCount++;
            } catch (BusinessException e) {
                // Already exists or other business logic, skip
            }
        }

        System.out.println("Monthly Machine KPI creation completed. Created " +
                createdCount + " new KPIs for " + year
                + "-" + month);
    }

}
