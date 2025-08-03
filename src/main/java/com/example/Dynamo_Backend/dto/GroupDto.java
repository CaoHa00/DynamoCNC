package com.example.Dynamo_Backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GroupDto {
    private String groupId;
    private String groupName;
    private String groupType;
    private List<StaffGroupDto> staffGroups;
    private List<MachineGroupDto> machineGroups;
    private String createdDate;
    private String updatedDate;
}
