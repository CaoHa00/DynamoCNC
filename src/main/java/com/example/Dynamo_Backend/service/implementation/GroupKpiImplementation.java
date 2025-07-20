package com.example.Dynamo_Backend.service.implementation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.Dynamo_Backend.dto.GroupKpiDto;
import com.example.Dynamo_Backend.entities.Group;
import com.example.Dynamo_Backend.entities.GroupKpi;
import com.example.Dynamo_Backend.mapper.GroupKpiMapper;
import com.example.Dynamo_Backend.repository.GroupKpiRepository;
import com.example.Dynamo_Backend.service.GroupKpiService;

public class GroupKpiImplementation implements GroupKpiService {
    @Autowired
    GroupKpiRepository groupKpiRepository;

    @Override
    public GroupKpiDto addGroupKpi(GroupKpiDto groupKpiDto) {
        long createdTimestamp = System.currentTimeMillis();
        GroupKpi groupKpi = GroupKpiMapper.mapToGroupKpi(groupKpiDto);
        groupKpi.setCreatedDate(createdTimestamp);
        groupKpi.setUpdatedDate(createdTimestamp);

        GroupKpi saveGroupKpi = groupKpiRepository.save(groupKpi);
        return GroupKpiMapper.mapToGroupKpiDto(saveGroupKpi);
    }

    @Override
    public GroupKpiDto updateGroupKpi(Integer Id, GroupKpiDto groupKpiDto) {
        GroupKpi groupKpi = groupKpiRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("GroupKpi is not found:" + Id));
        long updatedTimestamp = System.currentTimeMillis();

        groupKpi.setUpdatedDate(updatedTimestamp);
        groupKpi.setWeek(groupKpiDto.getWeek());
        groupKpi.setMonth(groupKpiDto.getMonth());
        groupKpi.setYear(groupKpiDto.getYear());
        groupKpi.setWorkHoursAim(groupKpiDto.getWorkHoursAim());
        groupKpi.setWorkHoursChange(groupKpiDto.getWorkHoursChange());
        groupKpi.setRealWorkHours(groupKpiDto.getRealWorkHours());

        GroupKpi saveGroupKpi = groupKpiRepository.save(groupKpi);
        return GroupKpiMapper.mapToGroupKpiDto(saveGroupKpi);
    }

    @Override
    public GroupKpiDto getGroupKpiById(Integer Id) {
        GroupKpi groupKpi = groupKpiRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("GroupKpi is not found:" + Id));
        return GroupKpiMapper.mapToGroupKpiDto(groupKpi);
    }

    @Override
    public void deleteGroupKpi(Integer Id) {
        GroupKpi groupKpi = groupKpiRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("GroupKpi is not found:" + Id));
        groupKpiRepository.delete(groupKpi);
    }

    @Override
    public List<GroupKpiDto> getGroupKpis() {
        List<GroupKpi> groupKpis = groupKpiRepository.findAll();
        return groupKpis.stream().map(GroupKpiMapper::mapToGroupKpiDto).toList();
    }

}
