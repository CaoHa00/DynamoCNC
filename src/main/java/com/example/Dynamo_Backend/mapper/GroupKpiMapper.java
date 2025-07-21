package com.example.Dynamo_Backend.mapper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.example.Dynamo_Backend.dto.GroupKpiDto;
import com.example.Dynamo_Backend.entities.GroupKpi;

public class GroupKpiMapper {
    public static GroupKpi mapToGroupKpi(GroupKpiDto groupKpiDto) {
        GroupKpi groupKpi = new GroupKpi();
        groupKpi.setId(groupKpiDto.getId());
        groupKpi.setWeek(groupKpiDto.getWeek());
        groupKpi.setMonth(groupKpiDto.getMonth());
        groupKpi.setYear(groupKpiDto.getYear());
        groupKpi.setWorkingHourGoal(groupKpiDto.getWorkingHourGoal());
        groupKpi.setWorkingHourDifference(groupKpiDto.getWorkingHourDifference());
        groupKpi.setWorkingHour(groupKpiDto.getWorkingHour());
        groupKpi.setIsMonth(groupKpiDto.getIsMonth());
        return groupKpi;
    }

    public static GroupKpiDto mapToGroupKpiDto(GroupKpi groupKpi) {
        String formattedCreatedDate = Instant.ofEpochMilli(groupKpi.getCreatedDate())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String formattedUpdatedDate = Instant.ofEpochMilli(groupKpi.getCreatedDate())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        GroupKpiDto groupKpiDto = new GroupKpiDto();
        groupKpiDto.setId(groupKpi.getId());
        groupKpiDto.setWeek(groupKpi.getWeek());
        groupKpiDto.setMonth(groupKpi.getMonth());
        groupKpiDto.setYear(groupKpi.getYear());
        groupKpiDto.setCreatedDate(formattedCreatedDate);
        groupKpiDto.setUpdatedDate(formattedUpdatedDate);
        groupKpiDto.setWorkingHourGoal(groupKpi.getWorkingHourGoal());
        groupKpiDto.setWorkingHourDifference(groupKpi.getWorkingHourDifference());
        groupKpiDto.setWorkingHour(groupKpi.getWorkingHour());
        groupKpiDto.setGroupId(groupKpi.getGroup().getGroupId());
        groupKpiDto.setIsMonth(groupKpi.getIsMonth());
        return groupKpiDto;
    }
}
