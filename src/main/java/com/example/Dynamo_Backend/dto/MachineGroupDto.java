package com.example.Dynamo_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MachineGroupDto {
    private String machineGroupId;
    private String groupId;
    private Integer machineId;
    private String machineName;
    private String createdDate;
    private String updatedDate;
}
