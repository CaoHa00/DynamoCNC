package com.example.Dynamo_Backend.dto.ResponseDto;

import java.util.List;

import com.example.Dynamo_Backend.dto.StaffKpiDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StaffGroupStatisticDto {
    private String groupId;
    private String groupName;
    private Integer staffCount;
    private Float workingHours;
    private Float workingRate;
    private Integer manufacturingPoints;
    private Float mpRate;
    private Integer processCount;
    private Float processRate;
    private Float totalKpi;
    private Float kpiRate;

    private List<StaffKpiDto> staffDto;

}
