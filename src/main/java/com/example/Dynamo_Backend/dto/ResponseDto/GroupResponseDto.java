package com.example.Dynamo_Backend.dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GroupResponseDto {
    private String groupId;
    private String groupName;
    private String groupType;
    private String createdDate;
    private String updatedDate;
}
