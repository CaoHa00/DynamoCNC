package com.example.Dynamo_Backend.service.implementation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.TimePeriodInfo;
import com.example.Dynamo_Backend.dto.RequestDto.StatisticRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.HistoryProcessDto;
import com.example.Dynamo_Backend.dto.ResponseDto.StaffDetailStatisticDto;
import com.example.Dynamo_Backend.dto.ResponseDto.StaffGroupStatisticDto;
import com.example.Dynamo_Backend.dto.ResponseDto.StaffSummary;
import com.example.Dynamo_Backend.dto.ResponseDto.StaffWorkingStatisticDto;
import com.example.Dynamo_Backend.entities.CurrentStatus;
import com.example.Dynamo_Backend.entities.Group;
import com.example.Dynamo_Backend.entities.GroupKpi;
import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.entities.MachineKpi;
import com.example.Dynamo_Backend.entities.OperateHistory;
import com.example.Dynamo_Backend.entities.Staff;
import com.example.Dynamo_Backend.entities.StaffKpi;
import com.example.Dynamo_Backend.exception.BusinessException;
import com.example.Dynamo_Backend.mapper.StaffKpiMapper;
import com.example.Dynamo_Backend.repository.*;
import com.example.Dynamo_Backend.service.ReportService;
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

    @Autowired
    ReportService reportService;

    @Override
    public StaffDetailStatisticDto getStaffDetailStatistic(StatisticRequestDto requestDto) {

        TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);
        TimePeriodInfo previousTimePeriodInfo = TimeRange.getPreviousTimeRange(timePeriodInfo);

        Staff staff = staffRepository.findByStaffId(requestDto.getId())
                .orElseThrow(() -> new BusinessException("Staff not found when get staff detail statistic"));

        Long processCount = 0L;
        Float totalKpi = 0f;
        Float totalWorkingHours = 0f;
        Long totalManufactoringPoints = 0L;
        Float previousTotalWorkingHours = 0f;
        Long previousTotalManufactoringPoints = 0l;
        Long previousProcessCount = 0l;
        Float previousTotalKpi = 0f;
        Long totalPgTime = 0l;
        Long previousTotalPgTime = 0l;
        List<String> ids = new ArrayList<>();
        ids.add(staff.getId());
        StaffSummary summary = operateHistoryRepository.getStaffKpi(timePeriodInfo.getStart(), timePeriodInfo.getEnd(),
                ids);
        totalPgTime = summary.getTotalPgTime() / 60;
        processCount = summary.getTotalProcess();
        totalWorkingHours = summary.getTotalDurationSeconds() / 3600f;
        totalManufactoringPoints = summary.getTotalManufacturingPoint();

        totalKpi = (float) ((totalManufactoringPoints * 6) + totalPgTime);

        summary = operateHistoryRepository.getStaffKpi(previousTimePeriodInfo.getStart(),
                previousTimePeriodInfo.getEnd(),
                ids);
        if (summary.getTotalPgTime() != null) {
            previousTotalPgTime = summary.getTotalPgTime() / 60;
            previousProcessCount = summary.getTotalProcess();
            previousTotalWorkingHours = summary.getTotalDurationSeconds() / 3600f;
            previousTotalManufactoringPoints = summary.getTotalManufacturingPoint();
            previousTotalKpi = (float) ((previousTotalManufactoringPoints * 6))
                    + previousTotalPgTime;
        }

        Float workingRate = 0f;
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
            workingRate = ((totalWorkingHours - previousTotalWorkingHours) / previousTotalWorkingHours) * 100;
        }
        if (previousTotalManufactoringPoints != 0) {
            mpRate = ((totalManufactoringPoints - previousTotalManufactoringPoints)
                    / (float) previousTotalManufactoringPoints) * 100;
        }

        StaffDetailStatisticDto staffDetailStatisticDto = new StaffDetailStatisticDto(
                staff.getStaffId(),
                staff.getStaffName(),
                Math.round(totalWorkingHours * 100.0) / 100.0f,
                Math.round(workingRate * 100.0) / 100.0f,
                totalManufactoringPoints,
                Math.round(mpRate * 100.0) / 100.0f,
                processCount,
                Math.round(processRate * 100.0) / 100.0f,
                totalKpi,
                Math.round(kpiRate * 100.0) / 100.0f);
        return staffDetailStatisticDto;

    }

    @Override
    public List<HistoryProcessDto> getStaffHistoryProcesses(StatisticRequestDto requestDto) {
        TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);
        Staff staff = staffRepository.findByStaffId(requestDto.getId())
                .orElseThrow(() -> new BusinessException("Staff not found when get history processes"));
        List<OperateHistory> operateHistories = operateHistoryRepository
                .findByStaffIdAndLogDate(staff.getId(), timePeriodInfo.getStart(), timePeriodInfo.getEnd());
        List<HistoryProcessDto> historyProcessDtos = new ArrayList<>();
        if (operateHistories.isEmpty()) {
            return List.of();
        }
        for (OperateHistory operateHistory : operateHistories) {

            HistoryProcessDto historyProcessDto = new HistoryProcessDto();
            historyProcessDto.setMachineName(operateHistory.getDrawingCodeProcess().getMachine().getMachineName());
            historyProcessDto.setOrderCode(operateHistory.getDrawingCodeProcess().getOrderDetail().getOrderCode());
            historyProcessDto.setPartNumber(operateHistory.getDrawingCodeProcess().getPartNumber());
            historyProcessDto.setStepNumber(operateHistory.getDrawingCodeProcess().getStepNumber());
            historyProcessDto.setStartTime(DateTimeUtil.convertTimestampToString(operateHistory.getStartTime()));
            historyProcessDto.setEndTime(DateTimeUtil.convertTimestampToString(operateHistory.getStopTime()));
            // historyProcessDto.setStaffIdNumber(staff.getStaffId());
            // historyProcessDto.setStaffName(staff.getStaffName());
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
        return historyProcessDtos;
    }

    @Override
    public StaffWorkingStatisticDto getStaffWorkingStatistic(StatisticRequestDto requestDto) {
        TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);

        Staff staff = null;
        StaffKpi staffKpi = null;
        List<StaffKpi> staffKpis;

        if (requestDto.getId() == null) {
            staffKpis = staffKpiRepository.findByGroup_groupIdAndMonthAndYear(
                    requestDto.getGroupId(), timePeriodInfo.getMonth(), timePeriodInfo.getYear());

            staff = staffRepository.findById(staffKpis.get(0).getStaff().getId())
                    .orElseThrow(() -> new BusinessException(
                            "Machine not found when get detail statistic with ID: "
                                    + requestDto.getId()));
        } else {
            staff = staffRepository.findByStaffId(requestDto.getId())
                    .orElseThrow(() -> new BusinessException(
                            "Staff not found when get detail statistic with ID: "
                                    + requestDto.getId()));
            staffKpi = staffKpiRepository.findByStaff_IdAndMonthAndYear(
                    staff.getId(), timePeriodInfo.getMonth(), timePeriodInfo.getYear());
            staffKpis = staffKpiRepository.findByGroup_groupIdAndMonthAndYear(
                    staffKpi.getGroup().getGroupId(), timePeriodInfo.getMonth(),
                    timePeriodInfo.getYear());
        }

        GroupKpi groupKpi;
        float workingHourReal = 0;
        int reportTime = 0;
        Long fromDate = DateTimeUtil.convertLocalDateToLong(timePeriodInfo.getStart());
        Long toDate = DateTimeUtil.convertLocalDateToLong(timePeriodInfo.getEnd());
        if (timePeriodInfo.isMonth()) {
            groupKpi = groupKpiRepository.findByGroup_GroupIdAndIsMonthAndMonthAndYear(
                    requestDto.getGroupId(), 1, timePeriodInfo.getMonth(), timePeriodInfo.getYear())
                    .orElseGet(GroupKpi::new);
        } else {
            int a = timePeriodInfo.getWeekOfYear();
            groupKpi = groupKpiRepository.findByGroup_GroupIdAndWeekAndYear(
                    requestDto.getGroupId(), timePeriodInfo.getWeekOfYear(),
                    timePeriodInfo.getYear())
                    .orElseGet(GroupKpi::new);
        }
        // check lại
        reportTime = reportService.calculateReport(fromDate, toDate, "FULL");
        reportTime = 0;
        workingHourReal = groupKpi.getWorkingHour() + reportTime;
        if (timePeriodInfo.getDay() == 1) {
            workingHourReal = workingHourReal / 7;
        }
        Long uniqueProcesses = 0l;
        float totalWorkingHours = 0f;
        long totalManufactoringPoints = 0l;
        Long totalPgTime = 0L;
        List<String> ids = new ArrayList<>();
        ids.add(staffKpi.getStaff().getId());
        float kpi = 0f;
        float ole = 0f;

        StaffSummary summary = operateHistoryRepository.getStaffKpi(timePeriodInfo.getStart(),
                timePeriodInfo.getEnd(), ids);
        if (summary.getTotalDurationSeconds() != null) {
            totalManufactoringPoints = summary.getTotalManufacturingPoint();
            totalPgTime = summary.getTotalPgTime() / 60;
            totalWorkingHours = summary.getTotalDurationSeconds() / 3600f;

            uniqueProcesses = summary.getTotalProcess();

            if (totalPgTime != 0f) {
                kpi = (float) (totalManufactoringPoints * 6 + totalPgTime);
            }
            ole = (((summary.getTotalManufacturingPoint() * 10) / 60) / workingHourReal);
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
                    0f, staffKpis.stream().map(StaffKpiMapper::mapToStaffDto).toList());
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
                Math.round(staffKpi.getKpi() * 100.0) / 100.0f,
                staffKpis.stream().map(StaffKpiMapper::mapToStaffDto).toList());
        return staffWorkingStatisticDto;
    }

    @Override
    public ByteArrayInputStream exportExcel(StatisticRequestDto requestDto) throws IOException {
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

        HashMap<String, List<Float>> dataMap = aggregateDataForPeriod(requestDto, unit);
        String[] headers = { periodLabel, "Tổng giờ làm", "Tổng điểm", "Số nguyên công", "Tổng giờ PG", "KPI" };
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
            for (String period : dataMap.keySet()) {
                List<Float> values = dataMap.get(period);
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(period);
                row.createCell(1).setCellValue(values.get(0)); // totalWorkingHours
                row.createCell(2).setCellValue(values.get(1)); // totalManufactoringPoints
                row.createCell(3).setCellValue(values.get(2)); // uniqueProcesses.size()
                row.createCell(4).setCellValue(values.get(3)); // totalPgTime
                row.createCell(5).setCellValue(values.get(4)); // kpi
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    private HashMap<String, List<Float>> aggregateDataForPeriod(StatisticRequestDto requestDto, ChronoUnit unit) {
        LocalDate startDate = LocalDate.parse(requestDto.getStartDate().substring(0, 10));
        LocalDate endDate = LocalDate.parse(requestDto.getEndDate().substring(0, 10));

        HashMap<String, List<Float>> map = new HashMap<>();
        Staff staff = staffRepository.findByStaffId(requestDto.getId())
                .orElseThrow(() -> new BusinessException("Staff not found when export excel"));
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            float totalWorkingHours = 0f;
            int totalManufactoringPoints = 0;
            Float totalPgTime = 0f;
            Set<String> uniqueProcesses = new HashSet<>();
            Long periodStartTimestamp = currentDate.atStartOfDay().toInstant(java.time.ZoneOffset.UTC).toEpochMilli();
            LocalDate nextPeriod = currentDate.plus(1, unit);
            Long periodEndTimestamp = nextPeriod.atStartOfDay().toInstant(java.time.ZoneOffset.UTC).toEpochMilli() - 1;

            List<OperateHistory> operateHistories = operateHistoryRepository.findByStaff_Id(staff.getId());
            if (!operateHistories.isEmpty()) {
                for (OperateHistory operateHistory : operateHistories) {
                    if (operateHistory.getStopTime() >= periodStartTimestamp
                            && operateHistory.getStopTime() <= periodEndTimestamp) {
                        totalManufactoringPoints += operateHistory.getManufacturingPoint();
                        totalPgTime += operateHistory.getPgTime() != null ? operateHistory.getPgTime() : 0f;
                        totalWorkingHours += (operateHistory.getStopTime() - operateHistory.getStartTime()) / 3600000f;
                        uniqueProcesses.add(operateHistory.getDrawingCodeProcess().getProcessId());
                    }
                }
            }
            Float kpi = 0f;
            if (totalPgTime != 0f) {
                kpi = totalManufactoringPoints * 6 / totalPgTime;
            }
            List<Float> values = new ArrayList<>();
            values.add(totalWorkingHours);
            values.add((float) totalManufactoringPoints);
            values.add((float) uniqueProcesses.size());
            values.add(totalPgTime);
            values.add(kpi);

            String periodKey;
            if (unit == ChronoUnit.DAYS) {
                periodKey = currentDate.toString();
            } else if (unit == ChronoUnit.WEEKS) {
                periodKey = "Tuần " + (currentDate.getDayOfYear() / 7 + 1);
            } else {
                periodKey = currentDate.getMonth().toString() + " " + currentDate.getYear();
            }
            map.put(periodKey, values);
            currentDate = nextPeriod;
        }
        return map;
    }
}
