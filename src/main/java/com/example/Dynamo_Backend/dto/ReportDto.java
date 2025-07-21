package com.example.Dynamo_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {
    private Integer Id;
    private String dateTime;
    private String office;
    private String reportType;
    private Integer hourDiff;
    private String createdDate;
    private String groupId;
    private String adminId;
}
