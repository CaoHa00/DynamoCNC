package com.example.Dynamo_Backend.service.implementation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.TimePeriodInfo;
import com.example.Dynamo_Backend.dto.RequestDto.GroupEfficiencyRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.StaffGroupOverviewDto;
import com.example.Dynamo_Backend.dto.ResponseDto.StaffGroupStatisticDto;
import com.example.Dynamo_Backend.dto.ResponseDto.StaffSummary;
import com.example.Dynamo_Backend.entities.Group;
import com.example.Dynamo_Backend.entities.GroupKpi;
import com.example.Dynamo_Backend.entities.StaffKpi;
import com.example.Dynamo_Backend.exception.BusinessException;
import com.example.Dynamo_Backend.mapper.StaffKpiMapper;
import com.example.Dynamo_Backend.repository.GroupKpiRepository;
import com.example.Dynamo_Backend.repository.GroupRepository;
import com.example.Dynamo_Backend.repository.OperateHistoryRepository;
import com.example.Dynamo_Backend.repository.StaffKpiRepository;
import com.example.Dynamo_Backend.service.GroupStatisticService;
import com.example.Dynamo_Backend.service.ReportService;
import com.example.Dynamo_Backend.util.TimeRange;

@Service
public class StaffGroupStatisticImplementation implements GroupStatisticService {
    @Autowired
    GroupRepository groupRepository;

    @Autowired
    GroupKpiRepository groupKpiRepository;

    @Autowired
    StaffKpiRepository staffKpiRepository;

    @Autowired
    OperateHistoryRepository operateHistoryRepository;

    @Autowired
    ReportService reportService;

    @Override

    public StaffGroupStatisticDto getGroupStatistic(GroupEfficiencyRequestDto requestDto) {
        TimePeriodInfo timePeriod = TimeRange.getRangeTypeAndWeek(requestDto);

        Group group = groupRepository.findById(requestDto.getGroupId())
                .orElseThrow(() -> new BusinessException("Group not found when get staff group statistic"));
        List<StaffKpi> staffKpiList = staffKpiRepository.findByGroup_groupIdAndMonthAndYear(
                group.getGroupId(), timePeriod.getMonth(), timePeriod.getYear());

        if (staffKpiList.isEmpty()) {
            return new StaffGroupStatisticDto(null, null, 0, 0l, 0f, 0L, 0f, 0L, 0f, 0f, 0f,
                    staffKpiList.stream().map(StaffKpiMapper::mapToStaffDto).toList());
        }
        Long processCount = 0L;
        Float totalKpi = 0f;
        Integer staffCount = staffKpiList.size();
        Float totalWorkingHours = 0f;
        Long totalManufactoringPoints = 0L;
        Float previousTotalWorkingHours = 0f;
        Long previousTotalManufactoringPoints = 0l;
        Long previousProcessCount = 0l;
        Float previousTotalKpi = 0f;
        Long totalPgTime = 0l;
        Long previousTotalPgTime = 0l;

        List<String> staffIds = staffKpiList.stream()
                .map(kpi -> kpi.getStaff().getId())
                .distinct()
                .toList();

        StaffSummary summary = operateHistoryRepository.getStaffKpi(timePeriod.getStart(), timePeriod.getEnd(),
                staffIds);

        totalPgTime = summary.getTotalPgTime() / 60;
        processCount = summary.getTotalProcess();
        totalWorkingHours = summary.getTotalDurationSeconds() / 2 / 3600f;
        totalManufactoringPoints = summary.getTotalManufacturingPoint();

        totalKpi = (float) ((totalManufactoringPoints * 6) / staffIds.size()) + (totalPgTime / staffIds.size());
        TimePeriodInfo previousTime = TimeRange.getPreviousTimeRange(timePeriod);
        List<StaffKpi> previousStaffKpiList = staffKpiRepository.findByGroup_groupIdAndMonthAndYear(
                group.getGroupId(), previousTime.getMonth(), previousTime.getYear());
        if (previousStaffKpiList.isEmpty()) {
            return new StaffGroupStatisticDto(group.getGroupId(), group.getGroupName(), 0,
                    totalPgTime, 0f, totalManufactoringPoints, 0f, 0L, 0f, 0f, 0f,
                    staffKpiList.stream().map(StaffKpiMapper::mapToStaffDto).toList());
        }

        staffIds = staffKpiList.stream()
                .map(kpi -> kpi.getStaff().getId())
                .distinct()
                .toList();

        summary = operateHistoryRepository.getStaffKpi(previousTime.getStart(), previousTime.getEnd(),
                staffIds);
        previousTotalPgTime = summary.getTotalPgTime() / 60;
        previousProcessCount = summary.getTotalProcess();
        previousTotalWorkingHours = summary.getTotalDurationSeconds() / 3600f;
        previousTotalManufactoringPoints = summary.getTotalManufacturingPoint();
        previousTotalKpi = (float) ((previousTotalManufactoringPoints * 6) / staffIds.size())
                + (previousTotalPgTime / staffIds.size());

        Float pgRate = 0f;
        Float mpRate = 0f;
        Float kpiRate = 0f;
        Float processRate = 0f;
        if (previousTotalKpi != 0) {
            kpiRate = ((totalKpi - previousTotalKpi) / previousTotalKpi) * 100;
        }
        if (previousProcessCount != 0) {
            processRate = (float) ((processCount - previousProcessCount) / previousProcessCount) * 100;
        }
        if (previousTotalWorkingHours != 0) {
            pgRate = (float) ((totalPgTime - previousTotalPgTime) / previousTotalPgTime) * 100;
        }
        if (previousTotalManufactoringPoints != 0) {
            mpRate = ((totalManufactoringPoints - previousTotalManufactoringPoints)
                    / (float) previousTotalManufactoringPoints) * 100;
        }
        return new StaffGroupStatisticDto(group.getGroupId(), group.getGroupName(), staffCount,
                totalPgTime, pgRate, totalManufactoringPoints, mpRate,
                processCount, processRate, totalKpi, kpiRate,
                staffKpiList.stream().map(StaffKpiMapper::mapToStaffDto).toList());
    }

    @Override
    public List<StaffGroupOverviewDto> getGroupOverview(GroupEfficiencyRequestDto requestDto) {

        TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);
        List<StaffKpi> staffKpiList = staffKpiRepository.findByGroup_groupIdAndMonthAndYear(
                requestDto.getGroupId(), timePeriodInfo.getMonth(), timePeriodInfo.getYear());
        if (staffKpiList.isEmpty()) {
            return List.of();
        }

        Group group = groupRepository.findById(requestDto.getGroupId())
                .orElseThrow(() -> new BusinessException("Group not found when get staff group overview"));
        GroupKpi groupKpi;
        float workingHourReal = 0;
        int reportTime = 0;
        Long fromDate = timePeriodInfo.getStartDate();
        Long toDate = timePeriodInfo.getEndDate();
        if (timePeriodInfo.isMonth()) {
            groupKpi = groupKpiRepository.findByGroup_GroupIdAndIsMonthAndMonthAndYear(
                    group.getGroupId(), 1, timePeriodInfo.getMonth(), timePeriodInfo.getYear())
                    .orElseThrow(() -> new BusinessException("GroupKPI not found when get staff group overview"));
        } else {
            groupKpi = groupKpiRepository.findByGroup_GroupIdAndYearAndWeekAndIsMonth(
                    group.getGroupId(), timePeriodInfo.getYear(), timePeriodInfo.getWeekOfYear(), 0)
                    .orElseThrow(() -> new BusinessException("GroupKPI not found when get staff group overview"));
        }
        // check lại
        reportTime = reportService.calculateReport(fromDate, toDate, "FULL");

        workingHourReal = groupKpi.getWorkingHour() + reportTime;
        if (timePeriodInfo.getDay() == 1) {
            workingHourReal = workingHourReal / 7;
        }

        List<StaffGroupOverviewDto> overviewDtos = new ArrayList<>();

        for (StaffKpi staffKpi : staffKpiList) {
            System.out.println(staffKpi.getStaff().getId());
            Float totalWorkingHours = 0f;
            Float totalMachineTime = 0f;
            Long totalManufactoringPoints = 0L;
            Long totalPgTime = 0l;
            Long uniqueProcesses = 0L;
            Float kpi = 0f;
            Float ole = 0f;
            List<String> ids = new ArrayList<>();
            ids.add(staffKpi.getStaff().getId());

            StaffSummary summary = operateHistoryRepository.getStaffKpi(timePeriodInfo.getStart(),
                    timePeriodInfo.getEnd(), ids);
            if (summary.getTotalDurationSeconds() != null) {
                totalManufactoringPoints = summary.getTotalManufacturingPoint();
                totalPgTime = summary.getTotalPgTime() / 60;
                totalWorkingHours = summary.getTotalDurationSeconds() / 2 / 3600f;
                totalMachineTime = summary.getTotalDurationSeconds() / 2 / 3600f;

                uniqueProcesses = summary.getTotalProcess();

                if (totalPgTime != 0f) {
                    kpi = (float) (totalManufactoringPoints * 6 + totalPgTime);
                    kpi = (float) Math.round(kpi);
                }
                ole = (((summary.getTotalManufacturingPoint() * 10) / 60) / workingHourReal);
            }
            overviewDtos.add(new StaffGroupOverviewDto(
                    staffKpi.getStaff().getId(),
                    staffKpi.getStaff().getStaffId(),
                    staffKpi.getStaff().getShortName(),
                    staffKpi.getWorkGoal(),
                    (float) Math.round(totalWorkingHours * 100) / 100,
                    (long) Math.round(staffKpi.getManufacturingPoint()),
                    totalManufactoringPoints,
                    uniqueProcesses, staffKpi.getOleGoal(), ole,
                    staffKpi.getKpi(), kpi, staffKpi.getMachineTimeGoal(),
                    (float) Math.round(totalMachineTime * 100) / 100,
                    staffKpi.getPgTimeGoal(), totalPgTime));
        }
        return overviewDtos;

        // Set<String> uniqueProcesses = new HashSet<>();

        // List<OperateHistory> operateHistories = operateHistoryRepository
        // .findByStaff_Id(staffKpi.getStaff().getId());
        // if (!operateHistories.isEmpty()) {
        // for (OperateHistory operateHistory : operateHistories) {
        // if (operateHistory.getStopTime() >= timePeriodInfo.getStartDate()
        // && operateHistory.getStopTime() <= timePeriodInfo.getEndDate()) {
        // totalManufactoringPoints += operateHistory.getManufacturingPoint();
        // totalPgTime += operateHistory.getPgTime() != null ?
        // operateHistory.getPgTime() : 0f;
        // totalWorkingHours += (operateHistory.getStopTime() -
        // operateHistory.getStartTime()) / 3600000f;
        // totalMachineTime += (operateHistory.getStopTime() -
        // operateHistory.getStartTime()) / 3600000f;
        // uniqueProcesses.add(operateHistory.getDrawingCodeProcess().getProcessId());
        // }
        // }
        // }

    }

    // cho xuất file
    public HashMap<String, List<StaffGroupOverviewDto>> calculateDataByDay(GroupEfficiencyRequestDto requestDto) {
        String startDate = requestDto.getStartDate().concat(" 00:00:00");
        String endDate = requestDto.getEndDate().concat(" 23:59:59");
        requestDto.setStartDate(startDate);
        requestDto.setEndDate(endDate);

        TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);
        HashMap<String, List<StaffGroupOverviewDto>> overviewDtos = aggregateDataForPeriod(requestDto.getGroupId(),
                timePeriodInfo, requestDto);
        return overviewDtos;
    }

    // cho xuất file
    private HashMap<String, List<StaffGroupOverviewDto>> aggregateDataForPeriod(String groupId,
            TimePeriodInfo timePeriodInfo, GroupEfficiencyRequestDto requestDto) {
        LocalDate startDate = LocalDate.parse(requestDto.getStartDate().substring(0, 10));
        LocalDate endDate = LocalDate.parse(requestDto.getEndDate().substring(0, 10));

        HashMap<String, List<StaffGroupOverviewDto>> map = new HashMap<>();
        List<StaffKpi> staffKpiList = staffKpiRepository.findByGroup_groupIdAndMonthAndYear(groupId,
                timePeriodInfo.getMonth(), timePeriodInfo.getYear());
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            List<StaffGroupOverviewDto> overviewDtos = new ArrayList<>();
            // Long dayStartTimestamp =
            // currentDate.atStartOfDay().toInstant(java.time.ZoneOffset.UTC).toEpochMilli();
            // Long dayEndTimestamp = currentDate.atTime(23, 59,
            // 59).toInstant(java.time.ZoneOffset.UTC).toEpochMilli();
            for (StaffKpi staffKpi : staffKpiList) {
                Float totalWorkingHours = 0f;
                Float totalMachineTime = 0f;
                long totalManufactoringPoints = 0;
                Long totalPgTime = 0l;
                List<String> ids = new ArrayList<>();
                ids.add(staffKpi.getStaff().getId());
                StaffSummary summary = operateHistoryRepository.getStaffKpi(endDate, currentDate, ids);
                totalMachineTime = summary.getTotalDurationSeconds() / 3600f;
                totalManufactoringPoints = summary.getTotalManufacturingPoint();
                totalPgTime = summary.getTotalPgTime();
                Long uniqueProcesses = summary.getTotalProcess();
                Float kpi = 0f;
                if (totalPgTime != 0f) {
                    kpi = (float) totalManufactoringPoints * 6 / totalPgTime;
                }
                overviewDtos.add(new StaffGroupOverviewDto(
                        staffKpi.getStaff().getId(),
                        staffKpi.getStaff().getStaffId(),
                        staffKpi.getStaff().getShortName(),
                        staffKpi.getWorkGoal(),
                        (float) Math.round(totalWorkingHours * 100) / 100,
                        (long) Math.round(staffKpi.getManufacturingPoint()),
                        totalManufactoringPoints,
                        uniqueProcesses, staffKpi.getOleGoal(), 0f,
                        staffKpi.getKpi(), kpi, staffKpi.getMachineTimeGoal(),
                        (float) Math.round(totalMachineTime * 100) / 100,
                        staffKpi.getPgTimeGoal(), totalPgTime));
            }
            map.put(currentDate.toString(), overviewDtos);
            currentDate = currentDate.plusDays(1);
        }
        return map;
    }

    // Cho xuất file
    private HashMap<String, List<StaffGroupOverviewDto>> aggregateDataForPeriod(String groupId,
            TimePeriodInfo timePeriodInfo, ChronoUnit unit, GroupEfficiencyRequestDto requestDto) {
        LocalDate startDate = LocalDate.parse(requestDto.getStartDate().substring(0, 10));
        LocalDate endDate = LocalDate.parse(requestDto.getEndDate().substring(0, 10));

        HashMap<String, List<StaffGroupOverviewDto>> map = new HashMap<>();
        List<StaffKpi> staffKpiList = staffKpiRepository.findByGroup_groupIdAndMonthAndYear(groupId,
                timePeriodInfo.getMonth(), timePeriodInfo.getYear());
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            List<StaffGroupOverviewDto> overviewDtos = new ArrayList<>();
            Long periodStartTimestamp = currentDate.atStartOfDay().toInstant(java.time.ZoneOffset.UTC).toEpochMilli();
            LocalDate nextPeriod = currentDate.plus(1, unit);
            Long periodEndTimestamp = nextPeriod.atStartOfDay().toInstant(java.time.ZoneOffset.UTC).toEpochMilli() - 1;
            for (StaffKpi staffKpi : staffKpiList) {
                float totalWorkingHours = 0f;
                float totalMachineTime = 0f;
                Long totalManufactoringPoints = 0l;
                Long totalPgTime = 0l;
                Float kpi = 0f;
                List<String> ids = new ArrayList<>();
                ids.add(staffKpi.getStaff().getId());

                StaffSummary staffSummary = operateHistoryRepository.getStaffKpi(nextPeriod, currentDate, ids);
                Long uniqueProcesses = staffSummary.getTotalProcess();
                totalWorkingHours = staffSummary.getTotalDurationSeconds();
                totalMachineTime = totalWorkingHours;
                totalManufactoringPoints = staffSummary.getTotalManufacturingPoint();
                totalPgTime = staffSummary.getTotalPgTime();
                if (totalPgTime != 0f) {
                    kpi = (float) totalManufactoringPoints * 6 / totalPgTime;
                }

                overviewDtos.add(new StaffGroupOverviewDto(
                        staffKpi.getStaff().getId(),
                        staffKpi.getStaff().getStaffId(),
                        staffKpi.getStaff().getShortName(),
                        staffKpi.getWorkGoal(),
                        (float) Math.round(totalWorkingHours * 100) / 100,
                        (long) Math.round(staffKpi.getManufacturingPoint()),
                        totalManufactoringPoints,
                        uniqueProcesses, staffKpi.getOleGoal(), 0f,
                        staffKpi.getKpi(), kpi, staffKpi.getMachineTimeGoal(),
                        (float) Math.round(totalMachineTime * 100) / 100,
                        staffKpi.getPgTimeGoal(), totalPgTime));
            }
            String periodKey;
            if (unit == ChronoUnit.DAYS) {
                periodKey = currentDate.toString();
            } else if (unit == ChronoUnit.WEEKS) {
                periodKey = "Tuần " + (currentDate.getDayOfYear() / 7 + 1);
            } else {
                periodKey = currentDate.getMonth().toString() + " " + currentDate.getYear();
            }
            map.put(periodKey, overviewDtos);
            currentDate = nextPeriod;
        }
        return map;
    }

    // XUẤT FILE
    @Override
    public ByteArrayInputStream exportExcel(GroupEfficiencyRequestDto requestDto) throws IOException {
        LocalDate startDate = LocalDate.parse(requestDto.getStartDate().substring(0, 10));
        LocalDate endDate = LocalDate.parse(requestDto.getEndDate().substring(0, 10));
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        ChronoUnit unit;
        String periodLabel;
        if (days < 7) {
            unit = ChronoUnit.DAYS;
            periodLabel = "Ngày";
        } else if (days < 32) {
            unit = ChronoUnit.WEEKS;
            periodLabel = "Tuần";
        } else {
            unit = ChronoUnit.MONTHS;
            periodLabel = "Tháng";
        }

        HashMap<String, List<StaffGroupOverviewDto>> overviewDtos = aggregateDataForPeriod(requestDto.getGroupId(),
                TimeRange.getRangeTypeAndWeek(requestDto), unit, requestDto);
        String[] headers = { periodLabel, "Tên Nhân Viên", "Mã nhân viên", "Tổng giờ làm", "Tổng điểm",
                "Số nguyên công",
                "Tổng giờ PG" };
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Data");

            // Create header
            Row header = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Fill rows
            int rowNum = 1;
            for (String period : overviewDtos.keySet()) {
                List<StaffGroupOverviewDto> dtos = overviewDtos.get(period);
                for (StaffGroupOverviewDto dto : dtos) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(period);
                    row.createCell(1).setCellValue(dto.getStaffFullName());
                    row.createCell(2).setCellValue(dto.getStaffIdNumber());
                    row.createCell(3).setCellValue(dto.getTotalWorkingHour());
                    row.createCell(4).setCellValue(dto.getTotalManufacturingPoint());
                    row.createCell(5).setCellValue(dto.getTotalOperationNumber());
                    row.createCell(6).setCellValue(dto.getPgTime() != null ? dto.getPgTime() : 0f);
                }
            }
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }

    }

}
