package com.example.Dynamo_Backend.service.implementation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.Dynamo_Backend.dto.TimePeriodInfo;
import com.example.Dynamo_Backend.dto.RequestDto.GroupEfficiencyRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.GroupEfficiencyResponseDto;
import com.example.Dynamo_Backend.entities.DrawingCodeProcess;
import com.example.Dynamo_Backend.entities.MachineKpi;
import com.example.Dynamo_Backend.entities.ProcessTime;
import com.example.Dynamo_Backend.exception.BusinessException;
import com.example.Dynamo_Backend.repository.MachineKpiRepository;
import com.example.Dynamo_Backend.service.GroupEfficiencyService;
import com.example.Dynamo_Backend.util.DateTimeUtil;

public class GroupEfficiencyImplementation implements GroupEfficiencyService {
    @Autowired
    private MachineKpiRepository machineKpiRepository;

    @Override
    public GroupEfficiencyResponseDto getGroupEfficiency(GroupEfficiencyRequestDto requestDto) {
        Long startTimestamp = DateTimeUtil.convertStringToTimestampDate(requestDto.getStartDate());
        Long endTimestamp = DateTimeUtil.convertStringToTimestampDate(requestDto.getEndDate());
        TimePeriodInfo timePeriodInfo = getRangeTypeAndWeek(requestDto);
        List<MachineKpi> kpiList = machineKpiRepository.findByGroup_groupIdAndMonthAndYear(
                requestDto.getGroupId(),
                timePeriodInfo.getMonth(),
                timePeriodInfo.getYear());
        Float totalRunTime = 0f;
        Float totalPgTime = 0f;
        Float mainAndElectricProductPgTime = 0f;
        Float otherProductPgTime = 0f;

        for (MachineKpi kpi : kpiList) {
            List<DrawingCodeProcess> processes = kpi.getMachine().getDrawingCodeProcesses();
            for (DrawingCodeProcess process : processes) {
                // filter process between start and end timestamp
                if (process.getStartTime() >= startTimestamp && process.getEndTime() <= endTimestamp) {
                    ProcessTime processTime = process.getProcessTime();
                    totalRunTime += processTime.getRunTime();
                    totalPgTime += processTime.getPgTime();
                    if (process.getProcessType().contains("chính") || process.getProcessType().contains("điện")) {
                        mainAndElectricProductPgTime += processTime.getPgTime();
                    } else {
                        otherProductPgTime += processTime.getPgTime();
                    }
                }
            }
        }
        if (timePeriodInfo.isMonth()) {
            // Handle monthly data retrieval
        } else {
            // Handle weekly data retrieval
        }

        throw new UnsupportedOperationException("Unimplemented method 'getGroupEfficiency'");
    }

    @Override
    public TimePeriodInfo getRangeTypeAndWeek(GroupEfficiencyRequestDto dto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(dto.getStartDate(), formatter);
        LocalDate end = LocalDate.parse(dto.getEndDate(), formatter);
        long days = ChronoUnit.DAYS.between(start, end) + 1;

        if (days <= 7 && start.getMonth() == end.getMonth()) {
            int weekOfMonth = start.get(WeekFields.of(Locale.getDefault()).weekOfMonth());
            return new TimePeriodInfo(false, weekOfMonth, start.getMonthValue(), start.getYear());
        } else if (start.getDayOfMonth() == 1 && end.equals(start.withDayOfMonth(start.lengthOfMonth()))) {
            return new TimePeriodInfo(true, null, start.getMonthValue(), start.getYear());
        } else {
            throw new BusinessException("Invalid date range");
        }
    }

}
