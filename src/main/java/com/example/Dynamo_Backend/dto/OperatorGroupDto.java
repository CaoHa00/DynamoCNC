package com.example.Dynamo_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperatorGroupDto {
    private String operatorGroupId;
    private String groupId;
    private String operatorId;
    private String operatorName;
    private int status;
    private String createdDate;
    private String updatedDate;
}
