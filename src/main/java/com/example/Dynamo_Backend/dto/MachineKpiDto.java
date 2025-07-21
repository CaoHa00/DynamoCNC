package com.example.Dynamo_Backend.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MachineKpiDto {
    private Integer Id;
    private Integer year;
    private Integer month;
    private Float oee;
    private Integer machineMiningTarget;
    private String createdDate;
    private String updatedDate;
    private Integer machineId;
}
