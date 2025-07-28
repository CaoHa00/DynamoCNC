package com.example.Dynamo_Backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffDto {
    private String Id;
    private Integer staffId;
    private String staffName;
    private String staffOffice;
    private String staffSection;
    private String shortName;
    private Integer status;
    private String createdDate;
    private String updatedDate;
    private String groupId;
    private StaffKpiDto staffKpiDtos;
}
