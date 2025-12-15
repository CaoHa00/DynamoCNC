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
        if (planDto.getStartTime() != null) {
            plan.setStartTime(DateTimeUtil.convertStringToTimestamp(planDto.getStartTime()));
        }
        if (planDto.getEndTime() != null) {
            plan.setEndTime(DateTimeUtil.convertStringToTimestamp(planDto.getEndTime()));
        }
        plan.setRemark(planDto.getRemark());
        if (planDto.getRemarkTime() == null) {
            plan.setRemarkTime(null);
        } else {
            plan.setRemarkTime(DateTimeUtil.convertStringToTimestamp(planDto.getRemarkTime()));
        }
        return plan;
    }

    public static PlanDto mapToPlanDto(Plan plan) {
        PlanDto planDto = new PlanDto();
        planDto.setId(plan.getId());
        planDto.setInProgress(plan.getInProgress());
        planDto.setStatus(plan.getStatus());
        if (plan.getStartTime() != null) {
            planDto.setStartTime(DateTimeUtil.convertTimestampToString(plan.getStartTime()));
        }
        if (plan.getEndTime() != null) {
            planDto.setEndTime(DateTimeUtil.convertTimestampToString(plan.getEndTime()));
        }
        if (plan.getRemark() == null) {
            planDto.setRemark(null);
        } else {
            planDto.setRemark(plan.getRemark());
        }
        if (plan.getRemarkTime() == null) {
            planDto.setRemarkTime(null);
        } else {
            planDto.setRemarkTime(DateTimeUtil.convertTimestampToString(plan.getRemarkTime()));
        }
        planDto.setMachineId(plan.getMachine().getMachineId());
        if (plan.getPlanner() != null) {
            planDto.setPlannerId(plan.getPlanner().getId());
        }

        planDto.setProcessId(plan.getDrawingCodeProcess().getProcessId());
        planDto.setStaffId(plan.getStaff().getStaffId());
        planDto.setCreatedDate(DateTimeUtil.convertTimestampToStringDate(plan.getCreatedDate()));
        planDto.setUpdatedDate(DateTimeUtil.convertTimestampToStringDate(plan.getUpdatedDate()));

        return planDto;
    }
}
