package com.example.Dynamo_Backend.dto.ResponseDto;

import java.util.List;

import com.example.Dynamo_Backend.dto.StaffDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListCurrentStaffStatusDto {
    private StaffDto staffDto;
    private List<CurrentStatusResponseDto> listStaffStatus;
}
