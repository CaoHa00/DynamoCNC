package com.example.Dynamo_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffGroupDto {
    private String staffGroupId;
    private String groupId;
    private String staffId;
    private String staffName;
    private int status;
    private String createdDate;
    private String updatedDate;
}
