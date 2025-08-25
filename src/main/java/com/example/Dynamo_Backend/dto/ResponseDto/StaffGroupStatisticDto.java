package com.example.Dynamo_Backend.dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StaffGroupStatisticDto {
    private String groupId;
    private String groupName;
    private String workingHours;
    private Integer manufacturingPoints;
    private Float workingRate;
    private Float mpRate;
}
