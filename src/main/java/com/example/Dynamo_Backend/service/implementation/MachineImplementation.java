package com.example.Dynamo_Backend.service.implementation;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Dynamo_Backend.dto.CurrentStaffDto;
import com.example.Dynamo_Backend.dto.CurrentStatusDto;
import com.example.Dynamo_Backend.dto.MachineDto;
import com.example.Dynamo_Backend.dto.MachineKpiDto;
import com.example.Dynamo_Backend.dto.StaffKpiDto;
import com.example.Dynamo_Backend.dto.RequestDto.MachineRequestDto;
import com.example.Dynamo_Backend.entities.CurrentStaff;
import com.example.Dynamo_Backend.entities.Group;
import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.entities.MachineKpi;
import com.example.Dynamo_Backend.entities.TempStartTime;
import com.example.Dynamo_Backend.exception.BusinessException;
import com.example.Dynamo_Backend.exception.ResourceNotFoundException;
import com.example.Dynamo_Backend.mapper.MachineKpiMapper;
import com.example.Dynamo_Backend.mapper.MachineMapper;
import com.example.Dynamo_Backend.repository.CurrentStaffRepository;
import com.example.Dynamo_Backend.repository.CurrentStatusRepository;
import com.example.Dynamo_Backend.repository.GroupRepository;
import com.example.Dynamo_Backend.repository.MachineKpiRepository;
import com.example.Dynamo_Backend.repository.MachineRepository;
import com.example.Dynamo_Backend.repository.TempStartTimeRepository;
import com.example.Dynamo_Backend.service.CurrentStaffService;
import com.example.Dynamo_Backend.service.CurrentStatusService;
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

    TempStartTimeRepository tempStartTimeRepository;

    CurrentStatusService currentStatusService;

    CurrentStaffRepository currentStaffRepository;

    @Override
    public MachineDto addMachine(MachineRequestDto machineDto) {
        int status = 0;
        long createdTimestamp = System.currentTimeMillis();
        Machine machine = MachineMapper.mapToMachine(machineDto);
        machine.setStatus(status);
        machine.setCreatedDate(createdTimestamp);
        machine.setUpdatedDate(createdTimestamp);
        machine.setMachineKpis(new ArrayList<MachineKpi>());
        Machine saveMachine = machineRepository.save(machine);

        machineDto.setMachineId(saveMachine.getMachineId());
        MachineKpiDto machineKpiDto = MachineKpiMapper.mapToMachineKpiDto(machineDto);
        machineKpiService.addMachineKpi(machineKpiDto);
        // saveMachine.setMachineKpis(new ArrayList<>());
        // saveMachine.getMachineKpis().add(MachineKpiMapper.mapToMachineKpi(saveMachineKpiDto));

        MachineDto result = MachineMapper.mapToMachineDto(saveMachine);

        // add current status for machine
        CurrentStatusDto currentStatus = new CurrentStatusDto();
        currentStatus.setMachineId(result.getMachineId());
        currentStatus.setStatus("0");
        currentStatusService.addCurrentStatus(currentStatus);
        TempStartTime tempStartTime = new TempStartTime();
        tempStartTime.setMachineId(saveMachine.getMachineId());
        tempStartTime.setStartTime(0);
        tempStartTimeRepository.save(tempStartTime);

        // CurrentStaff currentStaff = new CurrentStaff();
        // currentStaff.setMachine(saveMachine);
        // currentStaffRepository.save(currentStaff);

        // return MachineMapper.mapToMachineDto(saveMachine);
        return result;

    }

    @Override
    public MachineDto updateMachine(Integer Id, MachineRequestDto machineDto) {
        Machine machine = machineRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("Machine is not found:" + Id));
        long updatedTimestamp = System.currentTimeMillis();
        machine.setMachineWork(machineDto.getMachineWork());
        machine.setMachineName(machineDto.getMachineName());
        machine.setMachineOffice(machineDto.getMachineOffice());
        machine.setMachineType(machineDto.getMachineType());
        machine.setStatus(machine.getStatus());
        machine.setUpdatedDate(updatedTimestamp);
        MachineKpiDto machineKpiDto = MachineKpiMapper.mapToMachineKpiDto(machineDto);
        MachineKpi existingKpi = machineKpiRepository.findByMachine_machineIdAndMonthAndYear(Id, machineDto.getMonth(),
                machineDto.getYear());
        if (!existingKpi.isSameAs(machineKpiDto)) {
            machineKpiService.updateMachineKpiByMachineId(Id, machineKpiDto);
        }
        Machine updatedMachine = machineRepository.save(machine);
        return MachineMapper.mapToMachineDto(updatedMachine);
    }

    @Override
    public MachineDto getMachineById(Integer Id) {
        Machine machine = machineRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("Machine is not found:" + Id));
        return MachineMapper.mapToMachineDto(machine);
    }

    @Override
    public void deleteMachine(Integer Id) {
        Machine machine = machineRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("Machine is not found:" + Id));
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
                if (row.getRowNum() < 6)
                    continue;

                // Skip empty rows by checking if essential cells are empty
                String machineName = getCellStringValue(row, 2);
                String machineType = getCellStringValue(row, 3);
                String groupCell = getCellStringValue(row, 6);

                // Skip row if essential fields are empty
                if (machineName.trim().isEmpty() || machineType.trim().isEmpty() || groupCell.trim().isEmpty()) {
                    continue;
                }

                // String idCell = row.getCell(0).getStringCellValue();
                // String machineId = idCell.substring(idCell.length() - 3, idCell.length() -
                // 1);
                // machineDto.setMachineId(Integer.parseInt(machineId));

                Machine machine = new Machine();
                machine.setMachineName(machineName);
                machine.setMachineType(machineType);

                machine.setMachineWork(getCellStringValue(row, 4));
                machine.setMachineOffice(getCellStringValue(row, 5));
                machine.setStatus(1);
                Long createdTimestamp = System.currentTimeMillis();
                machine.setCreatedDate(createdTimestamp);
                machine.setUpdatedDate(createdTimestamp);
                Machine newMachine = machineRepository.save(machine);

                MachineKpi machineKpi = new MachineKpi();
                // set current month
                LocalDate now = LocalDate.now();
                machineKpi.setMonth(now.getMonthValue());
                machineKpi.setYear(now.getYear());
                machineKpi.setMachine(newMachine);
                Group group = groupRepository.findByGroupName(groupCell)
                        .orElseThrow(() -> new BusinessException(
                                "Group not found when import file excel with name: " + groupCell));
                machineKpi.setGroup(group);
                machineKpi.setMachineMiningTarget((float) getCellNumericValue(row, 7));
                machineKpi.setOee((float) getCellNumericValue(row, 8));
                machineKpi.setCreatedDate(createdTimestamp);
                machineKpi.setUpdatedDate(createdTimestamp);
                machineKpiRepository.save(machineKpi);
            }
            workbook.close();
            inputStream.close();
        } catch (Exception e) {
            throw new BusinessException("Failed to import machines from Excel file: " + e.getMessage());
        }
    }

    @Override
    public List<MachineDto> getActiveMachines() {
        List<Machine> machines = machineRepository.findAllByStatus(1);
        return machines.stream().map(MachineMapper::mapToMachineDto).toList();
    }

    private String getCellStringValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private double getCellNumericValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            return 0.0;
        }

        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            default:
                return 0.0;
        }
    }

}
