package com.example.Dynamo_Backend.mapper;

import com.example.Dynamo_Backend.dto.GroupKpiDto;
import com.example.Dynamo_Backend.entities.GroupKpi;
import com.example.Dynamo_Backend.util.DateTimeUtil;

public class GroupKpiMapper {
    public static GroupKpi mapToGroupKpi(GroupKpiDto groupKpiDto) {
        GroupKpi groupKpi = new GroupKpi();
        groupKpi.setId(groupKpiDto.getId());
        groupKpi.setOffice(groupKpiDto.getOffice());
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
        GroupKpiDto groupKpiDto = new GroupKpiDto();
        groupKpiDto.setId(groupKpi.getId());
        groupKpiDto.setWeek(groupKpi.getWeek());
        groupKpiDto.setMonth(groupKpi.getMonth());
        groupKpiDto.setYear(groupKpi.getYear());
        groupKpiDto.setOffice(groupKpi.getOffice());
        groupKpiDto.setCreatedDate(DateTimeUtil.convertTimestampToStringDate(groupKpi.getCreatedDate()));
        groupKpiDto.setUpdatedDate(DateTimeUtil.convertTimestampToStringDate(groupKpi.getCreatedDate()));
        groupKpiDto.setWorkingHourGoal(groupKpi.getWorkingHourGoal());
        groupKpiDto.setWorkingHourDifference(groupKpi.getWorkingHourDifference());
        groupKpiDto.setWorkingHour(groupKpi.getWorkingHour());
        groupKpiDto.setGroupId(groupKpi.getGroup().getGroupId());
        groupKpiDto.setIsMonth(groupKpi.getIsMonth());
        return groupKpiDto;
    }
}
