package com.example.Dynamo_Backend.mapper;

import com.example.Dynamo_Backend.dto.PlanDto;
import com.example.Dynamo_Backend.entities.Plan;
import com.example.Dynamo_Backend.util.DateTimeUtil;

public class PlanMapper {
    public static Plan mapToPlan(PlanDto planDto) {
        Plan plan = new Plan();
        plan.setId(planDto.getId());
        plan.setInProgress(planDto.getInProgress());
        plan.setStatus(planDto.getStatus());
        plan.setStartTime(DateTimeUtil.convertStringToTimestamp(planDto.getStartTime()));
        plan.setEndTime(DateTimeUtil.convertStringToTimestamp(planDto.getEndTime()));
        plan.setRemark(planDto.getRemark());
        plan.setRemarkTime(DateTimeUtil.convertStringToTimestamp(planDto.getRemarkTime()));
        return plan;
    }

    public static PlanDto mapToPlanDto(Plan plan) {
        PlanDto planDto = new PlanDto();
        planDto.setId(plan.getId());
        planDto.setInProgress(plan.getInProgress());
        planDto.setStatus(plan.getStatus());
        planDto.setStartTime(DateTimeUtil.convertTimestampToString(plan.getStartTime()));
        planDto.setEndTime(DateTimeUtil.convertTimestampToString(plan.getEndTime()));
        planDto.setRemark(plan.getRemark());
        planDto.setRemarkTime(DateTimeUtil.convertTimestampToString(plan.getRemarkTime()));
        planDto.setMachineId(plan.getMachine().getMachineId());
        planDto.setPlannerId(plan.getPlanner().getId());
        planDto.setProcessId(plan.getDrawingCodeProcess().getProcessId());
        planDto.setStaffId(plan.getStaff().getStaffId());
        planDto.setCreatedDate(DateTimeUtil.convertTimestampToStringDate(plan.getCreatedDate()));
        planDto.setUpdatedDate(DateTimeUtil.convertTimestampToStringDate(plan.getUpdatedDate()));

        return planDto;
    }
}
