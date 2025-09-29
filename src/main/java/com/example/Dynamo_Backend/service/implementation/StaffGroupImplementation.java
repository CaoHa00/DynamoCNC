package com.example.Dynamo_Backend.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.GroupDto;
import com.example.Dynamo_Backend.dto.StaffDto;
import com.example.Dynamo_Backend.dto.StaffGroupDto;
import com.example.Dynamo_Backend.entities.Group;
import com.example.Dynamo_Backend.entities.Staff;
import com.example.Dynamo_Backend.entities.StaffGroup;
import com.example.Dynamo_Backend.exception.ResourceNotFoundException;
import com.example.Dynamo_Backend.mapper.GroupMapper;

import com.example.Dynamo_Backend.mapper.StaffGroupMapper;
import com.example.Dynamo_Backend.mapper.StaffMapper;
import com.example.Dynamo_Backend.repository.StaffGroupRepository;
import com.example.Dynamo_Backend.service.GroupService;
import com.example.Dynamo_Backend.service.StaffGroupService;
import com.example.Dynamo_Backend.service.StaffService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class StaffGroupImplementation implements StaffGroupService {

    StaffGroupRepository staffGroupRepository;
    StaffService staffService;
    GroupService groupService;

    @Override
    public StaffGroupDto addStaffGroup(StaffGroupDto staffGroupDto) {

        StaffGroup staffGroup = StaffGroupMapper.mapToStaffGroup(staffGroupDto);
        StaffDto staff = staffService.getStaffById(staffGroupDto.getStaffId());

        GroupDto group = groupService.getGroupById(staffGroupDto.getGroupId());
        Group newGroup = GroupMapper.mapToGroup(group);

        Staff newStaff = StaffMapper.mapToStaff(staff);

        long createdTimestamp = System.currentTimeMillis();
        staffGroup.setCreatedDate(createdTimestamp);
        staffGroup.setUpdatedDate(createdTimestamp);
        staffGroup.setGroup(newGroup);
        staffGroup.setStaff(newStaff);

        StaffGroup saveStaffGroup = staffGroupRepository.save(staffGroup);
        return StaffGroupMapper.mapToStaffGroupDto(saveStaffGroup);
    }

    @Override
    public StaffGroupDto updateStaffGroup(String Id, StaffGroupDto staffGroupDto) {
        StaffGroup staffGroup = staffGroupRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("staffGroup is not found:" + Id));
        GroupDto group = groupService.getGroupById(staffGroupDto.getGroupId());
        StaffDto staff = staffService.getStaffById(staffGroupDto.getStaffId());
        Group updateGroup = GroupMapper.mapToGroup(group);
        Staff updateStaff = StaffMapper.mapToStaff(staff);
        long updatedTimestamp = System.currentTimeMillis();

        staffGroup.setUpdatedDate(updatedTimestamp);
        staffGroup.setStaff(updateStaff);
        staffGroup.setGroup(updateGroup);
        StaffGroup updatedstaffGroup = staffGroupRepository.save(staffGroup);
        return StaffGroupMapper.mapToStaffGroupDto(updatedstaffGroup);
    }

    @Override
    public StaffGroupDto getStaffGroupById(String Id) {
        StaffGroup staffGroup = staffGroupRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("staffGroup is not found:" + Id));
        return StaffGroupMapper.mapToStaffGroupDto(staffGroup);
    }

    @Override
    public void deleteStaffGroup(String Id) {
        StaffGroup staffGroup = staffGroupRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("StaffGroup is not found:" + Id));
        staffGroupRepository.delete(staffGroup);
    }

    @Override
    public List<StaffGroupDto> getStaffGroups() {
        List<StaffGroup> staffGroups = staffGroupRepository.findAll();
        return staffGroups.stream().map(StaffGroupMapper::mapToStaffGroupDto).toList();
    }

}
