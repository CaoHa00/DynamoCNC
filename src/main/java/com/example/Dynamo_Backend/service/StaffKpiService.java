package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.StaffKpiDto;

public interface StaffKpiService {
    StaffKpiDto addStaffKpi(StaffKpiDto staffKpiDto);

    StaffKpiDto updateStaffKpi(Integer Id, StaffKpiDto staffKpiDto);

    StaffKpiDto updateStaffKpiByStaffId(Integer staffId, StaffKpiDto staffKpiDto);

    StaffKpiDto getStaffKpiById(Integer staffId);

    void deleteStaffKpi(Integer Id);

    List<StaffKpiDto> getStaffKpis();
}
