package com.example.Dynamo_Backend.service.implementation;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.PlanDto;
import com.example.Dynamo_Backend.entities.Admin;
import com.example.Dynamo_Backend.entities.DrawingCodeProcess;
import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.entities.Plan;
import com.example.Dynamo_Backend.entities.Staff;
import com.example.Dynamo_Backend.mapper.PlanMapper;
import com.example.Dynamo_Backend.repository.AdminRepository;
import com.example.Dynamo_Backend.repository.DrawingCodeProcessRepository;
import com.example.Dynamo_Backend.repository.MachineRepository;
import com.example.Dynamo_Backend.repository.PlanRepository;
import com.example.Dynamo_Backend.repository.StaffRepository;
import com.example.Dynamo_Backend.service.PlanService;
import com.example.Dynamo_Backend.util.DateTimeUtil;

@Service
public class PlanImplementation implements PlanService {
        @Autowired
        private PlanRepository planRepository;

        @Autowired
        DrawingCodeProcessRepository drawingCodeProcessRepository;

        @Autowired
        StaffRepository staffRepository;

        @Autowired
        MachineRepository machineRepository;

        @Autowired
        private AdminRepository adminRepository;

        @Override
        public PlanDto addPlan(PlanDto planDto) {
                Plan plan = PlanMapper.mapToPlan(planDto);
                long createdTimestamp = System.currentTimeMillis();
                DrawingCodeProcess drawingCodeProcess = drawingCodeProcessRepository.findById(planDto.getProcessId())
                                .orElseThrow(() -> new RuntimeException(
                                                "DrawingCode Process is not found:" + planDto.getProcessId()));
                Staff staff = staffRepository.findByStaffId(planDto.getStaffId())
                                .orElseThrow(() -> new RuntimeException("Staff is not found:" + planDto.getStaffId()));
                Machine machine = machineRepository.findById(planDto.getMachineId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Machine is not found:" + planDto.getMachineId()));

                // after security, check if admin login
                if (planDto.getPlannerId() == null) {
                        plan.setPlanner(null);
                } else {
                        Admin admin = adminRepository.findById(planDto.getPlannerId())
                                        .orElse(null);
                        plan.setPlanner(admin);
                }

                plan.setDrawingCodeProcess(drawingCodeProcess);
                plan.setMachine(machine);
                plan.setStaff(staff);
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
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime startDateTime = LocalDateTime.parse(planDto.getStartTime(), formatter);
                long startDateTimestamp = startDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

                LocalDateTime endDate = LocalDateTime.parse(planDto.getEndTime(), formatter);
                long endDateTimestamp = endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                DrawingCodeProcess drawingCodeProcess = drawingCodeProcessRepository.findById(planDto.getProcessId())
                                .orElseThrow(() -> new RuntimeException(
                                                "DrawingCode Process is not found:" + planDto.getProcessId()));
                Staff staff = staffRepository.findByStaffId(planDto.getStaffId())
                                .orElseThrow(() -> new RuntimeException("Staff is not found:" + planDto.getStaffId()));
                Machine machine = machineRepository.findById(planDto.getMachineId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Machine is not found:" + planDto.getMachineId()));
                plan.setDrawingCodeProcess(drawingCodeProcess);
                plan.setInProgress(planDto.getInProgress());
                plan.setStatus(planDto.getStatus());
                plan.setStartTime(startDateTimestamp);
                plan.setEndTime(endDateTimestamp);
                plan.setRemark(planDto.getRemark());
                plan.setMachine(machine);
                plan.setStaff(staff);
                if (planDto.getRemarkTime() == null) {
                        plan.setRemarkTime(null);
                } else {
                        plan.setRemarkTime(DateTimeUtil.convertStringToTimestamp(planDto.getRemarkTime()));
                }
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
