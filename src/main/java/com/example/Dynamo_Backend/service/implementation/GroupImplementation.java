package com.example.Dynamo_Backend.service.implementation;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Dynamo_Backend.dto.CurrentStatusDto;
import com.example.Dynamo_Backend.dto.GroupDto;
import com.example.Dynamo_Backend.dto.ResponseDto.GroupResponseDto;
import com.example.Dynamo_Backend.entities.CurrentStatus;
import com.example.Dynamo_Backend.entities.Group;
import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.exception.BusinessException;
import com.example.Dynamo_Backend.exception.ResourceNotFoundException;
import com.example.Dynamo_Backend.mapper.GroupMapper;
import com.example.Dynamo_Backend.repository.CurrentStatusRepository;
import com.example.Dynamo_Backend.repository.GroupRepository;
import com.example.Dynamo_Backend.repository.MachineRepository;
import com.example.Dynamo_Backend.service.GroupService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GroupImplementation implements GroupService {
    @Autowired
    GroupRepository groupRepository;

    @Autowired
    MachineRepository machineRepository;

    @Autowired
    CurrentStatusRepository currentStatusRepository;

    @Override
    public GroupDto addGroup(GroupDto groupDto) {
        Group group = GroupMapper.mapToGroup(groupDto);
        long createdTimestamp = System.currentTimeMillis();
        group.setCreatedDate(createdTimestamp);
        group.setUpdatedDate(createdTimestamp);
        Group saveGroup = groupRepository.save(group);
        return GroupMapper.mapToGroupDto(saveGroup);
    }

    @Override
    public GroupDto updateGroup(String Id, GroupDto groupDto) {
        Group group = groupRepository.findById(Id)
                .orElseThrow(() -> new BusinessException("Group is not found:" + Id));
        long updatedTimestamp = System.currentTimeMillis();

        group.setUpdatedDate(updatedTimestamp);
        group.setGroupId(groupDto.getGroupId());
        group.setGroupName(groupDto.getGroupName());
        Group updatedgroup = groupRepository.save(group);
        return GroupMapper.mapToGroupDto(updatedgroup);
    }

    @Override
    public GroupDto getGroupById(String Id) {
        Group group = groupRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("Group is not found:" + Id));
        return GroupMapper.mapToGroupDto(group);
    }

    @Override
    public void deleteGroup(String Id) {
        Group group = groupRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("Group is not found:" + Id));
        groupRepository.delete(group);
    }

    @Override
    public List<GroupDto> getGroups() {
        List<Group> groups = groupRepository.findAll();
        groups.sort(Comparator.comparing(group -> {
            String name = group.getGroupName(); // ví dụ: "Group 10"
            String numberPart = name.replaceAll("\\D+", ""); // -> "10"
            return Integer.parseInt(numberPart); // -> 10
        }));
        return groups.stream().map(GroupMapper::mapToGroupDto).toList();
    }

    @Override
    public List<GroupDto> getStaffStatusGroup() {
        List<Group> groups = groupRepository.findAll();
        groups.sort(Comparator.comparing(group -> {
            String name = group.getGroupName();
            String numberPart = name.replaceAll("\\D+", "");
            return Integer.parseInt(numberPart);
        }));

        return groups.stream()
                .map(group -> GroupMapper.mapToGroupStatusDto(group))
                .toList();
    }

    @Override
    public List<GroupDto> getGroupByGroupType(String groupType) {
        List<Group> groups = groupRepository.findAll();
        return groups.stream().map(GroupMapper::mapToGroupDto).toList();
    }

    @Override
    public void importGroupFromExcel(MultipartFile file) {
        try {
            InputStream inputStream = ((MultipartFile) file).getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            List<Group> groups = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() < 6)
                    continue; // Skip header row
                Group group = new Group();

                Cell nameCell = row.getCell(2);
                if (nameCell == null || nameCell.getCellType() == CellType.BLANK)
                    continue;
                String groupName = nameCell.getStringCellValue().trim();
                if (groupName.isEmpty())
                    continue;

                group.setGroupName(groupName);

                long createdTimestamp = System.currentTimeMillis();
                group.setCreatedDate(createdTimestamp);
                group.setUpdatedDate(createdTimestamp);
                groups.add(group);
            }
            groupRepository.saveAll(groups);
            workbook.close();
            inputStream.close();
        } catch (Exception e) {
            throw new BusinessException("Failed to import groups from Excel file: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Long> getGroupCountByGroupId(String groupId) {
        int currentMonth = LocalDate.now().getMonthValue(); // 1 = January, 12 = December
        int currentYear = LocalDate.now().getYear();
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with ID: " + groupId));
        List<Machine> machines = machineRepository.findMachinesByGroupIdLatestOrCurrent(group.getGroupId(),
                currentMonth, currentYear);
        Map<String, Long> statusCount = machines.stream()
                .map(machine -> {
                    CurrentStatus status = currentStatusRepository
                            .findByMachineId(machine.getMachineId());
                    if (status == null)
                        return "Other";
                    String rawStatus = status.getStatus();
                    if ("R1".equals(rawStatus) || "R2".equals(rawStatus)) {
                        return "Run";
                    } else if ("S1".equals(rawStatus) || "S2".equals(rawStatus)) {
                        return "Stop";
                    } else {
                        return "Other";
                    }
                })
                .collect(Collectors.groupingBy(status -> status, Collectors.counting()));

        return statusCount;
    }

    @Override
    public GroupResponseDto getGroupByMachineId(String payload) {
        int currentMonth = LocalDate.now().getMonthValue(); // 1 = January, 12 = December
        int currentYear = LocalDate.now().getYear();
        String[] arr = payload.split("-");
        String machineId = arr[0];
        Integer machineIdInt = Integer.parseInt(machineId) + 1;
        String machineStr = String.format("%02d", machineIdInt);
        Group group = groupRepository.findLatestByMachineId(machineIdInt, currentMonth, currentYear)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found for machineId: " + machineStr));

        return GroupMapper.mapToGroupResponseDto(group);
    }
}
