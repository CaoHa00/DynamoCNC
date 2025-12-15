package com.example.Dynamo_Backend.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.Dynamo_Backend.dto.GroupKpiDto;

public interface GroupKpiService {
    GroupKpiDto addGroupKpi(GroupKpiDto groupKpiDto);

    GroupKpiDto updateGroupKpi(Integer Id, GroupKpiDto groupKpiDto);

    GroupKpiDto getGroupKpiById(Integer Id);

    void deleteGroupKpi(Integer Id);

    List<GroupKpiDto> getGroupKpis();

    void importGroupKpiWeekFromExcel(MultipartFile file);

    void importGroupKpiMonthFromExcel(MultipartFile file);

    List<GroupKpiDto> getGroupKpiByCurrentWeek();
}
