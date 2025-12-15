package com.example.Dynamo_Backend.dto.RequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class StatisticRequestDto {
    private String groupId;
    private Integer id;
    private String startDate;
    private String endDate;
}
