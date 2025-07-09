package com.example.Dynamo_Backend.dto;

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
    private String staffStep;
    private Double kpi;
    private Integer status;
    private String createdDate;
    private String updatedDate;
}
