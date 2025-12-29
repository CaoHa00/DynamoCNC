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
import com.example.Dynamo_Backend.entities.Group;
import com.example.Dynamo_Backend.entities.GroupKpi;
import com.example.Dynamo_Backend.entities.MachineKpi;
import com.example.Dynamo_Backend.entities.ProcessTime;
import com.example.Dynamo_Backend.exception.BusinessException;
import com.example.Dynamo_Backend.repository.DrawingCodeProcessRepository;
import com.example.Dynamo_Backend.repository.GroupKpiRepository;
import com.example.Dynamo_Backend.repository.GroupRepository;
import com.example.Dynamo_Backend.repository.MachineKpiRepository;
import com.example.Dynamo_Backend.repository.MachineRepository;
import com.example.Dynamo_Backend.service.GroupEfficiencyService;
import com.example.Dynamo_Backend.service.ReportService;
import com.example.Dynamo_Backend.util.DateTimeUtil;
import com.example.Dynamo_Backend.util.TimeRange;

@Service
public class GroupEfficiencyImplementation implements GroupEfficiencyService {
    @Autowired
    private MachineKpiRepository machineKpiRepository;

    @Autowired
    GroupKpiRepository groupKpiRepository;

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    DrawingCodeProcessRepository drawingCodeProcessRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ReportService reportService;

    @Override
    public GroupEfficiencyResponseDto getGroupEfficiency(GroupEfficiencyRequestDto requestDto) {
        String startDate = requestDto.getStartDate().concat(" 00:00:00"); // Should be "2025-07-21"
        String endDate = requestDto.getEndDate().concat(" 23:59:59");
        requestDto.setStartDate(startDate);
        requestDto.setEndDate(endDate);

        TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);
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
        Float processPgTime = 0f;
        Group group = groupRepository.findById(requestDto.getGroupId())
                .orElseThrow(() -> new BusinessException("Group not found with id: " + requestDto.getGroupId()));

        for (MachineKpi kpi : kpiList) {
            List<DrawingCodeProcess> processes = drawingCodeProcessRepository.findCompletedProcessesByMachineAndTime(
                    kpi.getMachine().getMachineId(), DateTimeUtil.convertStringToTimestamp(startDate),
                    DateTimeUtil.convertStringToTimestamp(endDate));
            for (DrawingCodeProcess process : processes) {
                ProcessTime processTime = process.getProcessTime();
                processPgTime += processTime.getPgTime();
                if (process.getProcessType().contains("Chính") || process.getProcessType().contains("Điện")) {
                    mainAndElectricProductPgTime += processTime.getPgTime();
                } else {
                    otherProductPgTime += processTime.getPgTime();
                }
            }

            List<Float> activeTime = machineRepository.calculateDurationsByStatusAndRange(
                    kpi.getMachine().getMachineId(), timePeriodInfo.getStartDate(),
                    timePeriodInfo.getEndDate());
            totalPgTime += activeTime.get(3);
            totalOffsetTime += activeTime.get(4);
            totalRunTime += activeTime.get(3) + totalOffsetTime;
            System.out.println(kpi.getMachine().getMachineId());
            System.out.println(processes.size());
        }

        float workingHourReal = 0;
        int reportTime = 0;
        Long fromDate = timePeriodInfo.getStartDate();
        Long toDate = timePeriodInfo.getEndDate();
        if (timePeriodInfo.isMonth()) {
            groupKpi = groupKpiRepository.findByGroup_GroupIdAndIsMonthAndMonthAndYear(
                    requestDto.getGroupId(), 1, timePeriodInfo.getMonth(), timePeriodInfo.getYear())
                    .orElseGet(GroupKpi::new);

        } else {
            int a = timePeriodInfo.getWeek();
            groupKpi = groupKpiRepository.findByGroup_GroupIdAndYearAndWeekAndIsMonth(
                    requestDto.getGroupId(), timePeriodInfo.getYear(),
                    timePeriodInfo.getWeekOfYear(), (int) 0).orElseGet(GroupKpi::new);
            reportTime = reportService.calculateReport(fromDate, toDate);
            workingHourReal = groupKpi.getWorkingHourGoal() + reportTime;

        }
        reportTime = reportService.calculateReport(fromDate, toDate);
        workingHourReal = groupKpi.getWorkingHourGoal() + reportTime;
        if (timePeriodInfo.getDay() == 1) {
            workingHourReal = workingHourReal / 7;
        }
        if (numberOfMachine > 0) {
            if (numberOfMachine > 0 && groupKpi.getWorkingHour() != null && groupKpi.getWorkingHour() > 0) {
                operationalEfficiency = (totalRunTime / (numberOfMachine * workingHourReal)) * 100;
            }
            if (totalRunTime > 0) {
                pgEfficiency = (totalPgTime / totalRunTime) * 100;
                offsetLoss = totalOffsetTime / totalRunTime * 100;
            }
            if (mainAndElectricProductPgTime > 0) {
                valueEfficiency = (mainAndElectricProductPgTime / processPgTime) * 100;
            }
            if (totalPgTime > 0) {
                otherLoss = otherProductPgTime / processPgTime * 100;
            }
            if (operationalEfficiency > 0 && pgEfficiency > 0 && valueEfficiency > 0) {
                oee = operationalEfficiency * pgEfficiency * valueEfficiency / 10000;
            }
        }

        return new GroupEfficiencyResponseDto(group.getGroupId(), group.getGroupName(),
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
        if (days > 31) {
            throw new BusinessException("Invalid date range");
        }
        if (days <= 7 && start.getMonth() == end.getMonth()) {
            int weekOfMonth = start.get(WeekFields.of(Locale.getDefault()).weekOfMonth());
            int weekOfYear = start.get(WeekFields.ISO.weekOfYear());
            return new TimePeriodInfo(false, weekOfMonth, start.getMonthValue(), start.getYear(), days, startTimestamp,
                    endTimestamp, weekOfYear);
        } else if (start.getDayOfMonth() == 1 && end.equals(start.withDayOfMonth(start.lengthOfMonth()))) {
            return new TimePeriodInfo(true, null, start.getMonthValue(), start.getYear(), days, startTimestamp,
                    endTimestamp, null);
        } else {
            throw new BusinessException("Invalid date range");
        }
    }

}
