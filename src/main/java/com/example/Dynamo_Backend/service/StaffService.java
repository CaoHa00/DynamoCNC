package com.example.Dynamo_Backend.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.Dynamo_Backend.dto.StaffDto;
import com.example.Dynamo_Backend.dto.RequestDto.StaffRequestDto;

public interface StaffService {
    StaffDto addStaff(StaffRequestDto staffRequestDto);

    StaffDto updateStaff(String Id, StaffRequestDto staffDto);

    void deleteStaff(List<String> ids);

    List<StaffDto> getAllStaffs();

    List<StaffDto> getAllStaffByStatus();

    StaffDto getStaffById(String Id);

    void importStaffFromExcel(MultipartFile file);

}
