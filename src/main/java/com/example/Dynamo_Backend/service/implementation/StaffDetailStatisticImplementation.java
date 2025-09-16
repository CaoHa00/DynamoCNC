package com.example.Dynamo_Backend.service.implementation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.TimePeriodInfo;
import com.example.Dynamo_Backend.dto.RequestDto.StatisticRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.HistoryProcessDto;
import com.example.Dynamo_Backend.dto.ResponseDto.StaffDetailStatisticDto;
import com.example.Dynamo_Backend.dto.ResponseDto.StaffWorkingStatisticDto;
import com.example.Dynamo_Backend.entities.CurrentStatus;
import com.example.Dynamo_Backend.entities.GroupKpi;
import com.example.Dynamo_Backend.entities.OperateHistory;
import com.example.Dynamo_Backend.entities.Staff;
import com.example.Dynamo_Backend.entities.StaffKpi;
import com.example.Dynamo_Backend.exception.BusinessException;
import com.example.Dynamo_Backend.repository.*;
import com.example.Dynamo_Backend.service.StaffDetailStatisticService;
import com.example.Dynamo_Backend.util.DateTimeUtil;
import com.example.Dynamo_Backend.util.TimeRange;

@Service
public class StaffDetailStatisticImplementation implements StaffDetailStatisticService {
    @Autowired
    GroupRepository groupRepository;

    @Autowired
    GroupKpiRepository groupKpiRepository;

    @Autowired
    StaffKpiRepository staffKpiRepository;

    @Autowired
    OperateHistoryRepository operateHistoryRepository;

    @Autowired
    StaffRepository staffRepository;

    @Autowired
    CurrentStatusRepository currentStatusRepository;

    @Override
    public StaffDetailStatisticDto getStaffDetailStatistic(StatisticRequestDto requestDto) {
        String startDate = requestDto.getStartDate().concat(" 00:00:00");
        String endDate = requestDto.getEndDate().concat(" 23:59:59");
        requestDto.setStartDate(startDate);
        requestDto.setEndDate(endDate);

        TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);
        TimePeriodInfo previousTimePeriodInfo = TimeRange.getPreviousTimeRange(timePeriodInfo);

        Staff staff = staffRepository.findByStaffId(requestDto.getId())
                .orElseThrow(() -> new BusinessException("Staff not found when get staff detail statistic"));

        float totalWorkingHours = 0f, previousWorkingHours = 0f;
        int totalManufactoringPoints = 0, previousManufactoringPoints = 0;
        Float totalPgTime = 0f, previousPgTime = 0f;
        Set<String> uniqueProcesses = new HashSet<>();
        Set<String> previousUniqueProcesses = new HashSet<>();

        List<OperateHistory> operateHistories = operateHistoryRepository
                .findByStaff_Id(staff.getId());
        if (!operateHistories.isEmpty()) {
            for (OperateHistory operateHistory : operateHistories) {
                if (operateHistory.getStopTime() >= timePeriodInfo.getStartDate()
                        && operateHistory.getStopTime() <= timePeriodInfo.getEndDate()) {
                    totalManufactoringPoints += operateHistory.getManufacturingPoint();
                    totalPgTime += operateHistory.getPgTime() != null ? operateHistory.getPgTime() : 0f;
                    totalWorkingHours += (operateHistory.getStopTime() - operateHistory.getStartTime()) / 3600000f;
                    uniqueProcesses.add(operateHistory.getDrawingCodeProcess().getProcessId());
                }
                if (operateHistory.getStopTime() >= previousTimePeriodInfo.getStartDate()
                        && operateHistory.getStopTime() <= previousTimePeriodInfo.getEndDate()) {
                    previousManufactoringPoints += operateHistory.getManufacturingPoint();
                    previousPgTime += operateHistory.getPgTime() != null ? operateHistory.getPgTime() : 0f;
                    previousWorkingHours += (operateHistory.getStopTime() - operateHistory.getStartTime()) / 3600000f;
                    previousUniqueProcesses.add(operateHistory.getDrawingCodeProcess().getProcessId());
                }
            }
        }
        Float kpi = 0f, previousKpi = 0f;
        if (totalPgTime != 0f) {
            kpi = totalManufactoringPoints * 6 / totalPgTime;
        }
        if (previousPgTime != 0f) {
            previousKpi = previousManufactoringPoints * 6 / previousPgTime;
        }
        Float workingRate = previousWorkingHours == 0f ? 0f
                : (totalWorkingHours - previousWorkingHours) / previousWorkingHours * 100;
        Float mpRate = previousManufactoringPoints == 0 ? 0f
                : (totalManufactoringPoints - previousManufactoringPoints) / (float) previousManufactoringPoints * 100;
        Float processRate = previousUniqueProcesses.size() == 0 ? 0f
                : (uniqueProcesses.size() - previousUniqueProcesses.size()) / (float) previousUniqueProcesses.size()
                        * 100;
        Float kpiRate = previousKpi == 0f ? 0f : (kpi - previousKpi) / previousKpi * 100;
        StaffDetailStatisticDto staffDetailStatisticDto = new StaffDetailStatisticDto(
                staff.getStaffId(),
                staff.getStaffName(),
                Math.round(totalWorkingHours * 100.0) / 100.0f,
                Math.round(workingRate * 100.0) / 100.0f,
                totalManufactoringPoints,
                Math.round(mpRate * 100.0) / 100.0f,
                uniqueProcesses.size(),
                Math.round(processRate * 100.0) / 100.0f,
                Math.round(kpi * 100.0) / 100.0f,
                Math.round(kpiRate * 100.0) / 100.0f);
        return staffDetailStatisticDto;
    }

    @Override
    public List<HistoryProcessDto> getStaffHistoryProcesses(StatisticRequestDto requestDto) {
        String startDate = requestDto.getStartDate().concat(" 00:00:00");
        String endDate = requestDto.getEndDate().concat(" 23:59:59");
        requestDto.setStartDate(startDate);
        requestDto.setEndDate(endDate);

        TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);
        Staff staff = staffRepository.findByStaffId(requestDto.getId())
                .orElseThrow(() -> new BusinessException("Staff not found when get history processes"));
        List<OperateHistory> operateHistories = operateHistoryRepository
                .findByStaff_Id(staff.getId());
        List<HistoryProcessDto> historyProcessDtos = new ArrayList<>();
        if (operateHistories.isEmpty()) {
            return List.of();
        }
        for (OperateHistory operateHistory : operateHistories) {
            if (operateHistory.getStopTime() >= timePeriodInfo.getStartDate()
                    && operateHistory.getStartTime() <= timePeriodInfo.getEndDate()
                    || operateHistory.getInProgress() == 1) {
                HistoryProcessDto historyProcessDto = new HistoryProcessDto();
                historyProcessDto.setMachineName(operateHistory.getDrawingCodeProcess().getMachine().getMachineName());
                historyProcessDto.setOrderCode(operateHistory.getDrawingCodeProcess().getOrderDetail().getOrderCode());
                historyProcessDto.setPartNumber(operateHistory.getDrawingCodeProcess().getPartNumber());
                historyProcessDto.setStepNumber(operateHistory.getDrawingCodeProcess().getStepNumber());
                historyProcessDto.setStartTime(DateTimeUtil.convertTimestampToString(operateHistory.getStartTime()));
                historyProcessDto.setEndTime(DateTimeUtil.convertTimestampToString(operateHistory.getStopTime()));
                historyProcessDto.setStaffIdNumber(staff.getStaffId());
                historyProcessDto.setStaffName(staff.getStaffName());
                String status = "";
                if (operateHistory.getDrawingCodeProcess().getProcessStatus() == 3
                        || operateHistory.getInProgress() == 0) {
                    status = "Completed";
                } else if (operateHistory.getInProgress() == 1) {
                    CurrentStatus currentStatus = currentStatusRepository
                            .findByMachineId(operateHistory.getDrawingCodeProcess().getMachine().getMachineId());
                    if (currentStatus != null) {
                        status = currentStatus.getStatus();
                    }
                    historyProcessDto.setEndTime(DateTimeUtil.convertTimestampToString(
                            System.currentTimeMillis()));
                }
                historyProcessDto.setStatus(status);
                historyProcessDtos.add(historyProcessDto);
            }
        }
        return historyProcessDtos;
    }

    @Override
    public StaffWorkingStatisticDto getStaffWorkingStatistic(StatisticRequestDto requestDto) {
        String startDate = requestDto.getStartDate().concat(" 00:00:00");
        String endDate = requestDto.getEndDate().concat(" 23:59:59");
        requestDto.setStartDate(startDate);
        requestDto.setEndDate(endDate);

        TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);

        Staff staff = staffRepository.findByStaffId(requestDto.getId())
                .orElseThrow(() -> new BusinessException("Staff not found when get staff detail statistic"));
        StaffKpi staffKpi = staffKpiRepository.findByStaff_IdAndMonthAndYear(
                staff.getId(), timePeriodInfo.getMonth(), timePeriodInfo.getYear());
        GroupKpi groupKpi;
        if (timePeriodInfo.isMonth()) {
            groupKpi = groupKpiRepository.findByGroup_GroupIdAndIsMonthAndMonthAndYear(
                    requestDto.getGroupId(), 1, timePeriodInfo.getMonth(), timePeriodInfo.getYear())
                    .orElseGet(GroupKpi::new);
        } else {
            groupKpi = groupKpiRepository.findByGroup_GroupIdAndWeekAndMonthAndYear(
                    requestDto.getGroupId(), timePeriodInfo.getWeek(), timePeriodInfo.getMonth(),
                    timePeriodInfo.getYear())
                    .orElseGet(GroupKpi::new);
        }
        float totalWorkingHours = 0f;
        int totalManufactoringPoints = 0;
        Float totalPgTime = 0f;
        Set<String> uniqueProcesses = new HashSet<>();

        List<OperateHistory> operateHistories = operateHistoryRepository
                .findByStaff_Id(staff.getId());
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
        Float kpi = 0f;
        Float ole = 0f;
        if (totalPgTime != 0f) {
            kpi = totalManufactoringPoints * 6 / totalPgTime;
        }
        if (groupKpi.getWorkingHour() != null && groupKpi.getWorkingHour() != 0f) {
            ole = ((totalManufactoringPoints * 10) / 60) / groupKpi.getWorkingHour();
        }
        if (staffKpi == null) {
            return new StaffWorkingStatisticDto(
                    staff.getStaffId(),
                    staff.getStaffName(),
                    totalManufactoringPoints,
                    0f,
                    Math.round(totalPgTime * 100.0) / 100.0f,
                    0f,
                    Math.round(totalWorkingHours * 100.0) / 100.0f,
                    0f,
                    Math.round(ole * 100.0) / 100.0f,
                    0f,
                    Math.round(kpi * 100.0) / 100.0f,
                    0f);
        }
        StaffWorkingStatisticDto staffWorkingStatisticDto = new StaffWorkingStatisticDto(
                staff.getStaffId(),
                staff.getStaffName(),
                totalManufactoringPoints,
                Math.round(staffKpi.getManufacturingPoint() * 100.0) / 100.0f,
                Math.round(totalPgTime * 100.0) / 100.0f,
                Math.round(staffKpi.getPgTimeGoal() * 100.0) / 100.0f,
                Math.round(totalWorkingHours * 100.0) / 100.0f,
                Math.round(staffKpi.getWorkGoal() * 100.0) / 100.0f,
                Math.round(ole * 100.0) / 100.0f,
                Math.round(staffKpi.getOleGoal() * 100.0) / 100.0f,
                Math.round(kpi * 100.0) / 100.0f,
                Math.round(staffKpi.getKpi() * 100.0) / 100.0f);
        return staffWorkingStatisticDto;
    }
}
