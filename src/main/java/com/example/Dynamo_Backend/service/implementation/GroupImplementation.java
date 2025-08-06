package com.example.Dynamo_Backend.service.implementation;

import java.io.InputStream;
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
import com.example.Dynamo_Backend.entities.CurrentStatus;
import com.example.Dynamo_Backend.entities.Group;
import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.mapper.GroupMapper;
import com.example.Dynamo_Backend.repository.CurrentStatusRepository;
import com.example.Dynamo_Backend.repository.GroupRepository;
import com.example.Dynamo_Backend.repository.MachineRepository;
import com.example.Dynamo_Backend.service.CurrentStatusService;
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
                .orElseThrow(() -> new RuntimeException("Group is not found:" + Id));
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
                .orElseThrow(() -> new RuntimeException("Group is not found:" + Id));
        return GroupMapper.mapToGroupDto(group);
    }

    @Override
    public void deleteGroup(String Id) {
        Group group = groupRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Group is not found:" + Id));
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
        List<Group> groups = groupRepository.findByGroupType(groupType);
        return groups.stream().map(GroupMapper::mapToGroupDto).toList();
    }

    @Override
    public void importGroupFromExcel(MultipartFile file) {
        try {
            InputStream inputStream = ((MultipartFile) file).getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0)
                    continue; // Skip header row
                Group group = new Group();
                group.setGroupType(row.getCell(0).getStringCellValue());
                group.setGroupName(row.getCell(1).getStringCellValue());
                long createdTimestamp = System.currentTimeMillis();
                group.setCreatedDate(createdTimestamp);
                group.setUpdatedDate(createdTimestamp);
                groupRepository.save(group);
            }
            workbook.close();
            inputStream.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to import groups from Excel file: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Long> getGroupCountByGroupId(String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with ID: " + groupId));
        List<Machine> machines = machineRepository.findByGroup_GroupId(group.getGroupId());
        Map<String, Long> statusCount = machines.stream()
                .map(machine -> {
                    CurrentStatus status = currentStatusRepository
                            .findByMachineId(machine.getMachineId());
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
}
