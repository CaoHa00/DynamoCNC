package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.GroupKpiDto;

public interface GroupKpiService {
    GroupKpiDto addGroupKpi(GroupKpiDto groupKpiDto);

    GroupKpiDto updateGroupKpi(Integer Id, GroupKpiDto groupKpiDto);

    GroupKpiDto getGroupKpiById(Integer Id);

    void deleteGroupKpi(Integer Id);

    List<GroupKpiDto> getGroupKpis();
}
