package com.example.Dynamo_Backend.service.implementation;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.PlanDto;
import com.example.Dynamo_Backend.entities.Plan;
import com.example.Dynamo_Backend.mapper.PlanMapper;
import com.example.Dynamo_Backend.repository.PlanRepository;
import com.example.Dynamo_Backend.service.PlanService;

@Service
public class PlanImplementation implements PlanService {
    @Autowired
    private PlanRepository planRepository;

    @Override
    public PlanDto addPlan(PlanDto planDto) {
        Plan plan = PlanMapper.mapToPlan(planDto);
        long createdTimestamp = System.currentTimeMillis();

        plan.setCreatedDate(createdTimestamp);
        plan.setUpdatedDate(createdTimestamp);
        Plan savedPlan = planRepository.save(plan);

        return PlanMapper.mapToPlanDto(savedPlan);
    }

    @Override
    public PlanDto updatePlan(Integer planId, PlanDto planDto) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan is not found:" + planId));
        long updatedTimestamp = System.currentTimeMillis();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(planDto.getStartTime(), formatter);
        long startDateTimestamp = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

        LocalDate endDate = LocalDate.parse(planDto.getEndTime(), formatter);
        long endDateTimestamp = endDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

        plan.setInProgress(planDto.getInProgress());
        plan.setStatus(planDto.getStatus());
        plan.setStartTime(startDateTimestamp);
        plan.setEndTime(endDateTimestamp);
        plan.setRemark(planDto.getRemark());
        plan.setRemarkTime(planDto.getRemarkTime());
        plan.setUpdatedDate(updatedTimestamp);

        Plan updatedPlan = planRepository.save(plan);
        return PlanMapper.mapToPlanDto(updatedPlan);
    }

    @Override
    public PlanDto getPlanById(Integer planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan is not found:" + planId));
        return PlanMapper.mapToPlanDto(plan);
    }

    @Override
    public void deletePlan(Integer planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan is not found:" + planId));
        planRepository.delete(plan);
    }

    @Override
    public List<PlanDto> getAllPlan() {
        List<Plan> plans = planRepository.findAll();
        return plans.stream().map(PlanMapper::mapToPlanDto).toList();
    }

}
