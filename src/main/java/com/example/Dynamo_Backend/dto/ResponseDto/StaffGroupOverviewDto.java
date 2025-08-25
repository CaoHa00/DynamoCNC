package com.example.Dynamo_Backend.dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class StaffGroupOverviewDto {
    private String staffId;
    private Integer staffIdNumber;
    private String staffFullName;
    private Float totalWorkingHour;
    private Integer totalManufacturingPoint;
    private Integer totalOperationNumber;// số nguyên công
    private Float kpi;
}
