package com.example.Dynamo_Backend.dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StaffDetailStatisticDto {
    private Integer staffId;
    private String staffName;
    private Float workingHours;
    private Float workingRate;
    private Integer manufacturingPoints;
    private Float mpRate;
    private Integer processCount;
    private Float processRate;
    private Float kpi;
    private Float kpiRate;
}
