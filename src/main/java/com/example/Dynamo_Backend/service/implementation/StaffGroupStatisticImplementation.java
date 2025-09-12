package com.example.Dynamo_Backend.service.implementation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.TimePeriodInfo;
import com.example.Dynamo_Backend.dto.RequestDto.GroupEfficiencyRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.StaffGroupOverviewDto;
import com.example.Dynamo_Backend.dto.ResponseDto.StaffGroupStatisticDto;
import com.example.Dynamo_Backend.entities.DrawingCodeProcess;
import com.example.Dynamo_Backend.entities.Group;
import com.example.Dynamo_Backend.entities.OperateHistory;
import com.example.Dynamo_Backend.entities.StaffKpi;
import com.example.Dynamo_Backend.exception.BusinessException;
import com.example.Dynamo_Backend.repository.GroupRepository;
import com.example.Dynamo_Backend.repository.OperateHistoryRepository;
import com.example.Dynamo_Backend.repository.StaffKpiRepository;
import com.example.Dynamo_Backend.service.GroupStatisticService;
import com.example.Dynamo_Backend.util.TimeRange;

@Service
public class StaffGroupStatisticImplementation implements GroupStatisticService {
    @Autowired
    GroupRepository groupRepository;

    @Autowired
    StaffKpiRepository staffKpiRepository;

    @Autowired
    OperateHistoryRepository operateHistoryRepository;

    @Override
    public StaffGroupStatisticDto getGroupStatistic(GroupEfficiencyRequestDto requestDto) {
        String startDate = requestDto.getStartDate().concat(" 00:00:00"); // Should be "2025-07-21"
        String endDate = requestDto.getEndDate().concat(" 23:59:59");
        requestDto.setStartDate(startDate);
        requestDto.setEndDate(endDate);

        TimePeriodInfo timePeriod = TimeRange.getRangeTypeAndWeek(requestDto);

        Group group = groupRepository.findById(requestDto.getGroupId())
                .orElseThrow(() -> new BusinessException("Group not found when get staff group statistic"));
        List<StaffKpi> staffKpiList = staffKpiRepository.findByGroup_groupIdAndMonthAndYear(
                group.getGroupId(), timePeriod.getMonth(), timePeriod.getYear());

        if (staffKpiList.isEmpty()) {
            return new StaffGroupStatisticDto(null, null, null, 0, 0f, 0f);
        }

        Float totalWorkingHours = 0f;
        Integer totalManufactoringPoints = 0;
        Float previousTotalWorkingHours = 0f;
        Integer previousTotalManufactoringPoints = 0;

        for (StaffKpi staffKpi : staffKpiList) {
            List<OperateHistory> operateHistories = operateHistoryRepository
                    .findByStaff_Id(staffKpi.getStaff().getId());
            if (operateHistories.isEmpty()) {
                continue;
            }
            for (OperateHistory operateHistory : operateHistories) {
                if (operateHistory.getStopTime() >= timePeriod.getStartDate()
                        && operateHistory.getStopTime() <= timePeriod.getEndDate()) {
                    totalWorkingHours += (operateHistory.getStopTime() - operateHistory.getStartTime()) / 3600000f;
                    totalManufactoringPoints += operateHistory.getManufacturingPoint();
                }
            }
        }

        TimePeriodInfo previousTime = TimeRange.getPreviousTimeRange(timePeriod);
        List<StaffKpi> previousStaffKpiList = staffKpiRepository.findByGroup_groupIdAndMonthAndYear(
                group.getGroupId(), previousTime.getMonth(), previousTime.getYear());
        if (previousStaffKpiList.isEmpty()) {
            return new StaffGroupStatisticDto(group.getGroupId(), group.getGroupName(),
                    totalWorkingHours.toString(), totalManufactoringPoints, 0f, 0f);
        }
        for (StaffKpi staffKpi : previousStaffKpiList) {
            List<OperateHistory> operateHistories = operateHistoryRepository
                    .findByStaff_Id(staffKpi.getStaff().getId());
            if (operateHistories.isEmpty()) {
                continue;
            }
            for (OperateHistory operateHistory : operateHistories) {
                if (operateHistory.getStopTime() >= previousTime.getStartDate()
                        && operateHistory.getStopTime() <= previousTime.getEndDate()) {
                    previousTotalWorkingHours += (operateHistory.getStopTime() - operateHistory.getStartTime())
                            / 3600000f;
                    previousTotalManufactoringPoints += operateHistory.getManufacturingPoint();
                }
            }
        }
        Float workingRate = 0f;
        Float mpRate = 0f;
        if (previousTotalWorkingHours != 0) {
            workingRate = ((totalWorkingHours - previousTotalWorkingHours) / previousTotalWorkingHours) * 100;
        }
        if (previousTotalManufactoringPoints != 0) {
            mpRate = ((totalManufactoringPoints - previousTotalManufactoringPoints)
                    / (float) previousTotalManufactoringPoints) * 100;
        }

        // transfer totalWorkingHours to String form "4h40m"
        int hours = totalWorkingHours.intValue();
        int minutes = Math.round((totalWorkingHours - hours) * 60);
        String workingHoursString = String.format("%dh%02dm", hours, minutes);
        return new StaffGroupStatisticDto(group.getGroupId(), group.getGroupName(),
                workingHoursString, totalManufactoringPoints, workingRate, mpRate);
    }

    @Override
    public List<StaffGroupOverviewDto> getGroupOverview(GroupEfficiencyRequestDto requestDto) {
        String startDate = requestDto.getStartDate().concat(" 00:00:00");
        String endDate = requestDto.getEndDate().concat(" 23:59:59");
        requestDto.setStartDate(startDate);
        requestDto.setEndDate(endDate);

        TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);
        List<StaffKpi> staffKpiList = staffKpiRepository.findByGroup_groupIdAndMonthAndYear(
                requestDto.getGroupId(), timePeriodInfo.getMonth(), timePeriodInfo.getYear());
        if (staffKpiList.isEmpty()) {
            return List.of();
        }

        List<StaffGroupOverviewDto> overviewDtos = new ArrayList<>();
        float totalWorkingHoursGroup = 0f;

        for (StaffKpi staffKpi : staffKpiList) {
            float totalWorkingHours = 0f;
            int totalManufactoringPoints = 0;
            Float totalPgTime = 0f;
            Set<String> uniqueProcesses = new HashSet<>();

            List<OperateHistory> operateHistories = operateHistoryRepository
                    .findByStaff_Id(staffKpi.getStaff().getId());
            if (!operateHistories.isEmpty()) {
                for (OperateHistory operateHistory : operateHistories) {
                    if (operateHistory.getStopTime() >= timePeriodInfo.getStartDate()
                            && operateHistory.getStopTime() <= timePeriodInfo.getEndDate()) {
                        totalManufactoringPoints += operateHistory.getManufacturingPoint();
                        totalPgTime += operateHistory.getPgTime() != null ? operateHistory.getPgTime() : 0f;
                        totalWorkingHours += (operateHistory.getStopTime() - operateHistory.getStartTime()) / 3600000f;
                        uniqueProcesses.add(operateHistory.getDrawingCodeProcess().getProcessId());
                    }
                }
            }
            totalWorkingHoursGroup += totalWorkingHours;
            Float kpi = 0f;
            if (totalPgTime != 0f) {
                kpi = totalManufactoringPoints * 6 / totalPgTime;
            }

            overviewDtos.add(new StaffGroupOverviewDto(
                    staffKpi.getStaff().getId(),
                    staffKpi.getStaff().getStaffId(),
                    staffKpi.getStaff().getStaffName(),
                    totalWorkingHours,
                    totalManufactoringPoints,
                    uniqueProcesses.size(), staffKpi.getOleGoal(), 0f,
                    staffKpi.getKpi(), kpi));
        }
        for (StaffGroupOverviewDto dto : overviewDtos) {
            if (totalWorkingHoursGroup != 0) {
                dto.setOle(((dto.getTotalManufacturingPoint() * 10) / 60) / totalWorkingHoursGroup);
            }
        }
        return overviewDtos;
    }

}
