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

import com.example.Dynamo_Backend.dto.MachineDailySummaryByMachine;
import com.example.Dynamo_Backend.dto.TimePeriodInfo;
import com.example.Dynamo_Backend.dto.RequestDto.GroupEfficiencyRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.GroupEfficiencyResponseDto;
import com.example.Dynamo_Backend.entities.Group;
import com.example.Dynamo_Backend.entities.GroupKpi;
import com.example.Dynamo_Backend.entities.MachineKpi;
import com.example.Dynamo_Backend.exception.BusinessException;
import com.example.Dynamo_Backend.repository.DrawingCodeProcessRepository;
import com.example.Dynamo_Backend.repository.GroupKpiRepository;
import com.example.Dynamo_Backend.repository.GroupRepository;
import com.example.Dynamo_Backend.repository.MachineDailyRepository;
import com.example.Dynamo_Backend.repository.MachineKpiRepository;
import com.example.Dynamo_Backend.service.GroupEfficiencyService;
import com.example.Dynamo_Backend.service.MachineKpiService;
import com.example.Dynamo_Backend.service.ReportService;
import com.example.Dynamo_Backend.util.DateTimeUtil;
import com.example.Dynamo_Backend.util.TimeRange;
import com.example.Dynamo_Backend.util.TypeUtil;

@Service
public class GroupEfficiencyImplementation implements GroupEfficiencyService {
    @Autowired
    private MachineKpiRepository machineKpiRepository;

    @Autowired
    GroupKpiRepository groupKpiRepository;

    @Autowired
    DrawingCodeProcessRepository drawingCodeProcessRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ReportService reportService;
    @Autowired
    private MachineKpiService machineKpiService;

    @Autowired
    private MachineDailyRepository machineDailyRepository;

    // report??
    @Override
    public GroupEfficiencyResponseDto getGroupEfficiency(GroupEfficiencyRequestDto requestDto) {
        TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);

        Float operationalEfficiency = 0f;
        Float pgEfficiency = 0f;
        Float valueEfficiency = 0f;
        Float oee = 0f;
        Float offsetLoss = 0f;
        Float otherLoss = 0f;
        Float totalRunTime = 0f; // daily.getRunPg+ daily.getoffset
        Float totalPgTime = 0f; // daily.getRunPg
        Float totalOffsetTime = 0f; // daily.getoffset
        Float mainAndElectricProductPgTime = 0f; // daily.getPGMainRun + daily+getPGElectric
        Float otherProductPgTime = 0f; // daily.getother
        GroupKpi groupKpi = null;
        Float processPgTime = 0f; // daily.getPGMainRun + daily+getPGElectric + daily.getother
        List<Integer> machineIds = machineKpiService.determineMachineByMonthOrWeek(requestDto.getGroupId(),
                timePeriodInfo);
        Group group = groupRepository.findById(requestDto.getGroupId())
                .orElseThrow(() -> new BusinessException("Group not found with id: " + requestDto.getGroupId()));
        Integer numberOfMachine = machineIds.size();

        MachineDailySummaryByMachine sum = machineDailyRepository.sumByMachines(machineIds, timePeriodInfo.getStart(),
                timePeriodInfo.getEnd(),
                requestDto.getShiftCode());

        processPgTime = (sum.mainProductPgSeconds() / 3600f) + (sum.electricPgSeconds() / 3600f)
                + (sum.otherSeconds() / 3600f);
        mainAndElectricProductPgTime = (sum.mainProductPgSeconds() / 3600f) + (sum.electricPgSeconds() / 3600f);
        otherProductPgTime = (sum.otherSeconds() / 3600f);

        totalPgTime = sum.runPgSeconds() / 3600f;
        totalOffsetTime = sum.runOffsetSeconds() / 3600f;
        totalRunTime = totalPgTime + totalOffsetTime;
        float workingHourReal = 0;
        int reportTime = 0;

        // fromDate và startDate check sau report
        Long fromDate = DateTimeUtil.convertLocalDateToLong(timePeriodInfo.getStart());
        Long toDate = DateTimeUtil.convertLocalDateToLong(timePeriodInfo.getEnd());
        if (timePeriodInfo.isMonth()) {
            groupKpi = groupKpiRepository.findByGroup_GroupIdAndIsMonthAndMonthAndYear(
                    requestDto.getGroupId(), 1, timePeriodInfo.getMonth(), timePeriodInfo.getYear())
                    .orElseGet(GroupKpi::new);

        } else {
            groupKpi = groupKpiRepository.findByGroup_GroupIdAndYearAndWeekAndIsMonth(
                    requestDto.getGroupId(), timePeriodInfo.getYear(),
                    timePeriodInfo.getWeekOfYear(), (int) 0).orElseGet(GroupKpi::new);
            reportTime = reportService.calculateReport(fromDate, toDate, requestDto.getShiftCode());
            workingHourReal = groupKpi.getWorkingHour() + reportTime;

        }
        reportTime = reportService.calculateReport(fromDate, toDate, requestDto.getShiftCode());
        workingHourReal = groupKpi.getWorkingHour() + reportTime;
        if (timePeriodInfo.getDay() == 1 && requestDto.getShiftCode().equals("FULL")) {
            workingHourReal = workingHourReal / 7;
        } else {
            workingHourReal = workingHourReal / 14;
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
            if (processPgTime > 0) {
                otherLoss = otherProductPgTime / processPgTime * 100;
            }
            if (operationalEfficiency > 0 && pgEfficiency > 0 && valueEfficiency > 0) {
                oee = operationalEfficiency * pgEfficiency * valueEfficiency / 10000;
            }
        }

        return new GroupEfficiencyResponseDto(group.getGroupId(), group.getGroupName(),
                operationalEfficiency, pgEfficiency, valueEfficiency, oee, offsetLoss, otherLoss);
    }

    // checksau
    @Override
    public TimePeriodInfo getRangeTypeAndWeek(GroupEfficiencyRequestDto dto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
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
                    endTimestamp, start,
                    end, weekOfYear);
        } else if (start.getDayOfMonth() == 1 && end.equals(start.withDayOfMonth(start.lengthOfMonth()))) {
            return new TimePeriodInfo(true, null, start.getMonthValue(), start.getYear(), days, startTimestamp,
                    endTimestamp, start,
                    end, null);
        } else {
            throw new BusinessException("Invalid date range");
        }
    }

}
