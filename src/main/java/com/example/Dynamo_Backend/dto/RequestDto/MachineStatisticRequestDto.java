package com.example.Dynamo_Backend.dto.RequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class MachineStatisticRequestDto {
    private String groupId;
    private Integer machineId;
    private String startDate;
    private String endDate;
}
