package com.example.Dynamo_Backend.service.implementation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.TimePeriodInfo;
import com.example.Dynamo_Backend.dto.RequestDto.GroupEfficiencyRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.GroupEfficiencyResponseDto;
import com.example.Dynamo_Backend.entities.DrawingCodeProcess;
import com.example.Dynamo_Backend.entities.GroupKpi;
import com.example.Dynamo_Backend.entities.MachineKpi;
import com.example.Dynamo_Backend.entities.ProcessTime;
import com.example.Dynamo_Backend.exception.BusinessException;
import com.example.Dynamo_Backend.repository.GroupKpiRepository;
import com.example.Dynamo_Backend.repository.MachineKpiRepository;
import com.example.Dynamo_Backend.service.GroupEfficiencyService;
import com.example.Dynamo_Backend.util.DateTimeUtil;

@Service
public class GroupEfficiencyImplementation implements GroupEfficiencyService {
    @Autowired
    private MachineKpiRepository machineKpiRepository;

    @Autowired
    GroupKpiRepository groupKpiRepository;

    @Override
    public GroupEfficiencyResponseDto getGroupEfficiency(GroupEfficiencyRequestDto requestDto) {
        String startDate = requestDto.getStartDate().concat(" 00:00:00"); // Should be "2025-07-21"
        String endDate = requestDto.getEndDate().concat(" 23:59:59");
        requestDto.setStartDate(startDate);
        requestDto.setEndDate(endDate);

        TimePeriodInfo timePeriodInfo = getRangeTypeAndWeek(requestDto);
        List<MachineKpi> kpiList = machineKpiRepository.findByGroup_groupIdAndMonthAndYear(
                requestDto.getGroupId(),
                timePeriodInfo.getMonth(),
                timePeriodInfo.getYear());
        Integer numberOfMachine = kpiList.size();
        Float operationalEfficiency = 0f;
        Float pgEfficiency = 0f;
        Float valueEfficiency = 0f;
        Float oee = 0f;
        Float offsetLoss = 0f;
        Float otherLoss = 0f;
        Float totalRunTime = 0f;
        Float totalPgTime = 0f;
        Float totalOffsetTime = 0f;
        Float mainAndElectricProductPgTime = 0f;
        Float otherProductPgTime = 0f;
        GroupKpi groupKpi = null;

        for (MachineKpi kpi : kpiList) {
            List<DrawingCodeProcess> processes = kpi.getMachine().getDrawingCodeProcesses();
            for (DrawingCodeProcess process : processes) {
                // if (process.getStartTime() >= timePeriodInfo.getStartDate()
                // && process.getEndTime() <= timePeriodInfo.getEndDate()) {
                if (process.getStartTime() <= timePeriodInfo.getEndDate() &&
                        process.getEndTime() >= timePeriodInfo.getStartDate()) {
                    ProcessTime processTime = process.getProcessTime();
                    if (processTime == null)
                        continue;
                    totalRunTime += processTime.getRunTime();
                    totalPgTime += processTime.getPgTime();
                    totalOffsetTime += processTime.getOffsetTime();
                    if (process.getProcessType().contains("chính") || process.getProcessType().contains("điện")) {
                        mainAndElectricProductPgTime += processTime.getPgTime();
                    } else {
                        otherProductPgTime += processTime.getPgTime();
                    }
                }
            }
        }
        if (timePeriodInfo.isMonth()) {
            groupKpi = groupKpiRepository.findByGroup_GroupIdAndIsMonthAndMonthAndYear(
                    requestDto.getGroupId(), 1, timePeriodInfo.getMonth(), timePeriodInfo.getYear())
                    .orElseThrow(() -> new BusinessException("Group KPI not found"));
        } else {
            groupKpi = groupKpiRepository.findByGroup_GroupIdAndWeekAndMonthAndYear(
                    requestDto.getGroupId(), timePeriodInfo.getWeek(), timePeriodInfo.getMonth(),
                    timePeriodInfo.getYear()).orElseThrow(() -> new BusinessException("Group KPI not found"));
        }
        if (numberOfMachine > 0) {
            if (numberOfMachine > 0 && groupKpi.getWorkingHour() > 0) {
                operationalEfficiency = (totalRunTime / (numberOfMachine * groupKpi.getWorkingHour())) * 100;
            }
            if (totalRunTime > 0) {
                pgEfficiency = (totalPgTime / totalRunTime) * 100;
                offsetLoss = totalOffsetTime / totalRunTime * 100;
            }
            if (mainAndElectricProductPgTime > 0) {
                valueEfficiency = (totalPgTime / mainAndElectricProductPgTime) * 100;
            }
            if (totalPgTime > 0) {
                otherLoss = otherProductPgTime / totalPgTime * 100;
            }
            if (operationalEfficiency > 0 && pgEfficiency > 0 && valueEfficiency > 0) {
                oee = operationalEfficiency * pgEfficiency * valueEfficiency / 10000;
            }
        }

        return new GroupEfficiencyResponseDto(groupKpi.getGroup().getGroupId(), groupKpi.getGroup().getGroupName(),
                operationalEfficiency, pgEfficiency, valueEfficiency, oee, offsetLoss, otherLoss);
    }

    @Override
    public TimePeriodInfo getRangeTypeAndWeek(GroupEfficiencyRequestDto dto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDate start = LocalDateTime.parse(dto.getStartDate(), formatter).toLocalDate();
        LocalDate end = LocalDateTime.parse(dto.getEndDate(), formatter).toLocalDate();
        long days = ChronoUnit.DAYS.between(start, end) + 1;
        Long startTimestamp = DateTimeUtil.convertStringToTimestamp(dto.getStartDate());
        Long endTimestamp = DateTimeUtil.convertStringToTimestamp(dto.getEndDate());
        if (days < 1 || days > 31) {
            throw new BusinessException("Invalid date range");
        }
        if (days <= 7 && start.getMonth() == end.getMonth()) {
            int weekOfMonth = start.get(WeekFields.of(Locale.getDefault()).weekOfMonth());
            return new TimePeriodInfo(false, weekOfMonth, start.getMonthValue(), start.getYear(), days, startTimestamp,
                    endTimestamp);
        } else if (start.getDayOfMonth() == 1 && end.equals(start.withDayOfMonth(start.lengthOfMonth()))) {
            return new TimePeriodInfo(true, null, start.getMonthValue(), start.getYear(), days, startTimestamp,
                    endTimestamp);
        } else {
            throw new BusinessException("Invalid date range");
        }
    }

}
