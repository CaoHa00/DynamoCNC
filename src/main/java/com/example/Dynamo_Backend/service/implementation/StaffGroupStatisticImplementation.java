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
import com.example.Dynamo_Backend.entities.Group;
import com.example.Dynamo_Backend.entities.GroupKpi;
import com.example.Dynamo_Backend.entities.OperateHistory;
import com.example.Dynamo_Backend.entities.Report;
import com.example.Dynamo_Backend.entities.StaffKpi;
import com.example.Dynamo_Backend.exception.BusinessException;
import com.example.Dynamo_Backend.mapper.StaffKpiMapper;
import com.example.Dynamo_Backend.repository.GroupKpiRepository;
import com.example.Dynamo_Backend.repository.GroupRepository;
import com.example.Dynamo_Backend.repository.OperateHistoryRepository;
import com.example.Dynamo_Backend.repository.StaffKpiRepository;
import com.example.Dynamo_Backend.service.GroupStatisticService;
import com.example.Dynamo_Backend.util.DateTimeUtil;
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
            return new StaffGroupStatisticDto(null, null, 0, 0f, 0f, 0, 0f, 0, 0f, 0f, 0f,
                    staffKpiList.stream().map(StaffKpiMapper::mapToStaffDto).toList());
        }
        Integer processCount = 0;
        Float totalKpi = 0f;
        Integer staffCount = staffKpiList.size();
        Float totalWorkingHours = 0f;
        Integer totalManufactoringPoints = 0;
        Float previousTotalWorkingHours = 0f;
        Integer previousTotalManufactoringPoints = 0;
        Integer previousProcessCount = 0;
        Float previousTotalKpi = 0f;

        for (StaffKpi staffKpi : staffKpiList) {
            Set<String> uniqueProcesses = new HashSet<>();
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
                    uniqueProcesses.add(operateHistory.getDrawingCodeProcess().getProcessId());
                }
            }
            processCount += uniqueProcesses.size();
            totalKpi += staffKpi.getKpi();
        }

        TimePeriodInfo previousTime = TimeRange.getPreviousTimeRange(timePeriod);
        List<StaffKpi> previousStaffKpiList = staffKpiRepository.findByGroup_groupIdAndMonthAndYear(
                group.getGroupId(), previousTime.getMonth(), previousTime.getYear());
        if (previousStaffKpiList.isEmpty()) {
            return new StaffGroupStatisticDto(group.getGroupId(), group.getGroupName(), 0,
                    totalWorkingHours, 0f, totalManufactoringPoints, 0f, 0, 0f, 0f, 0f,
                    staffKpiList.stream().map(StaffKpiMapper::mapToStaffDto).toList());
        }
        for (StaffKpi staffKpi : previousStaffKpiList) {
            Set<String> uniqueProcesses = new HashSet<>();
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
                    uniqueProcesses.add(operateHistory.getDrawingCodeProcess().getProcessId());
                }
            }
            previousProcessCount += uniqueProcesses.size();
            previousTotalKpi += staffKpi.getKpi();
        }
        Float workingRate = 0f;
        Float mpRate = 0f;
        Float kpiRate = 0f;
        Float processRate = 0f;
        if (previousTotalKpi != 0) {
            kpiRate = ((totalKpi - previousTotalKpi) / previousTotalKpi) * 100;
        }
        if (previousProcessCount != 0) {
            processRate = ((processCount - previousProcessCount) / (float) previousProcessCount) * 100;
        }
        if (previousTotalWorkingHours != 0) {
            workingRate = ((totalWorkingHours - previousTotalWorkingHours) / previousTotalWorkingHours) * 100;
        }
        if (previousTotalManufactoringPoints != 0) {
            mpRate = ((totalManufactoringPoints - previousTotalManufactoringPoints)
                    / (float) previousTotalManufactoringPoints) * 100;
        }

        // transfer totalWorkingHours to String form "4h40m"
        // int hours = totalWorkingHours.intValue();
        // int minutes = Math.round((totalWorkingHours - hours) * 60);
        // String workingHoursString = String.format("%dh%02dm", hours, minutes);
        return new StaffGroupStatisticDto(group.getGroupId(), group.getGroupName(), staffCount,
                totalWorkingHours, workingRate, totalManufactoringPoints, mpRate,
                processCount, processRate, totalKpi, kpiRate,
                staffKpiList.stream().map(StaffKpiMapper::mapToStaffDto).toList());
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
        Group group = groupRepository.findById(requestDto.getGroupId())
                .orElseThrow(() -> new BusinessException("Group not found when get staff group overview"));
        GroupKpi groupKpi;
        if (timePeriodInfo.isMonth()) {
            groupKpi = groupKpiRepository.findByGroup_GroupIdAndIsMonthAndMonthAndYear(
                    group.getGroupId(), 1, timePeriodInfo.getMonth(), timePeriodInfo.getYear())
                    .orElseThrow(() -> new BusinessException("GroupKPI not found when get staff group overview"));
        } else {
            groupKpi = groupKpiRepository.findByGroup_GroupIdAndWeekAndMonthAndYear(
                    group.getGroupId(), timePeriodInfo.getWeek(), timePeriodInfo.getMonth(), timePeriodInfo.getYear())
                    .orElseThrow(() -> new BusinessException("GroupKPI not found when get staff group overview"));
        }

        Integer reportCount = group.getReports() != null ? group.getReports().size() : 0;
        if (reportCount != 0) {
            Float workingHoursDiff = 0f;
            for (Report report : group.getReports()) {
                workingHoursDiff += report.getHourDiff() != null ? report.getHourDiff() : 0f;
            }
            groupKpi.setWorkingHour(groupKpi.getWorkingHourGoal() + workingHoursDiff);
            groupKpiRepository.save(groupKpi);
        }

        List<StaffGroupOverviewDto> overviewDtos = new ArrayList<>();

        for (StaffKpi staffKpi : staffKpiList) {
            float totalWorkingHours = 0f;
            float totalMachineTime = 0f;
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
                        totalMachineTime += (operateHistory.getStopTime() - operateHistory.getStartTime()) / 3600000f;
                        uniqueProcesses.add(operateHistory.getDrawingCodeProcess().getProcessId());
                    }
                }
            }
            Float kpi = 0f;
            if (totalPgTime != 0f) {
                kpi = totalManufactoringPoints * 6 / totalPgTime;
            }
            overviewDtos.add(new StaffGroupOverviewDto(
                    staffKpi.getStaff().getId(),
                    staffKpi.getStaff().getStaffId(),
                    staffKpi.getStaff().getStaffName(),
                    staffKpi.getWorkGoal(),
                    totalWorkingHours,
                    staffKpi.getManufacturingPoint(),
                    totalManufactoringPoints,
                    uniqueProcesses.size(), staffKpi.getOleGoal(), 0f,
                    staffKpi.getKpi(), kpi, staffKpi.getMachineTimeGoal(), totalMachineTime,
                    staffKpi.getPgTimeGoal(), totalPgTime));
        }
        for (StaffGroupOverviewDto dto : overviewDtos) {
            if (groupKpi.getWorkingHour() != 0) {
                dto.setOle(((dto.getTotalManufacturingPoint() * 10) / 60) / groupKpi.getWorkingHour());
            }
        }
        return overviewDtos;
    }

    public HashMap<String, List<StaffGroupOverviewDto>> calculateDataByDay(GroupEfficiencyRequestDto requestDto) {
        String startDate = requestDto.getStartDate().concat(" 00:00:00");
        String endDate = requestDto.getEndDate().concat(" 23:59:59");
        requestDto.setStartDate(startDate);
        requestDto.setEndDate(endDate);

        HashMap<String, StaffGroupOverviewDto> exportData = new HashMap<>();
        TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);
        long i = 0;
        if (timePeriodInfo.getDay() <= 7) {
            i = 1;
        } else if (timePeriodInfo.getDay() > 7 && timePeriodInfo.getDay() <= 31) {
            i = 7;
        } else {
            i = timePeriodInfo.getDay() / 12;
        }
        HashMap<String, List<StaffGroupOverviewDto>> overviewDtos = aggregateDataForPeriod(requestDto.getGroupId(),
                timePeriodInfo, i,
                requestDto);
        return overviewDtos;
    }

    private HashMap<String, List<StaffGroupOverviewDto>> aggregateDataForPeriod(String groupId,
            TimePeriodInfo timePeriodInfo, long days, GroupEfficiencyRequestDto requestDto) {
        LocalDate startDate = LocalDate.parse(requestDto.getStartDate().substring(0, 10));
        LocalDate endDate = LocalDate.parse(requestDto.getEndDate().substring(0, 10));

        HashMap<String, List<StaffGroupOverviewDto>> map = new HashMap<>();
        // Example: adjust repository query
        List<StaffKpi> staffKpiList = staffKpiRepository.findByGroup_groupIdAndMonthAndYear(groupId,
                timePeriodInfo.getMonth(), timePeriodInfo.getYear());
        LocalDate date = startDate;
        for (; date.getDayOfMonth() <= endDate.getDayOfMonth();) {
            List<StaffGroupOverviewDto> overviewDtos = new ArrayList<>();
            Long startTimestamp = DateTimeUtil.convertStringToTimestamp(requestDto.getStartDate());
            Long endTimestamp = DateTimeUtil.convertStringToTimestamp(requestDto.getEndDate());
            for (StaffKpi staffKpi : staffKpiList) {
                float totalWorkingHours = 0f;
                float totalMachineTime = 0f;
                int totalManufactoringPoints = 0;
                Float totalPgTime = 0f;
                Set<String> uniqueProcesses = new HashSet<>();
                List<OperateHistory> operateHistories = operateHistoryRepository
                        .findByStaff_Id(staffKpi.getStaff().getId());
                StaffGroupOverviewDto dtos = new StaffGroupOverviewDto();
                dtos.setStaffIdNumber(staffKpi.getStaff().getStaffId());
                dtos.setStaffFullName(staffKpi.getStaff().getStaffName());
                if (!operateHistories.isEmpty()) {
                    for (OperateHistory operateHistory : operateHistories) {
                        if (operateHistory.getStopTime() >= startTimestamp
                                && operateHistory.getStopTime() <= endTimestamp) {
                            totalManufactoringPoints += operateHistory.getManufacturingPoint();
                            totalPgTime += operateHistory.getPgTime() != null ? operateHistory.getPgTime() : 0f;
                            totalWorkingHours += (operateHistory.getStopTime() - operateHistory.getStartTime())
                                    / 3600000f;
                            totalMachineTime += (operateHistory.getStopTime() - operateHistory.getStartTime())
                                    / 3600000f;
                            uniqueProcesses.add(operateHistory.getDrawingCodeProcess().getProcessId());
                        }
                    }
                }
                Float kpi = 0f;
                if (totalPgTime != 0f) {
                    kpi = totalManufactoringPoints * 6 / totalPgTime;
                }
                overviewDtos = new ArrayList<>();
                overviewDtos.add(new StaffGroupOverviewDto(
                        staffKpi.getStaff().getId(),
                        staffKpi.getStaff().getStaffId(),
                        staffKpi.getStaff().getStaffName(),
                        staffKpi.getWorkGoal(),
                        totalWorkingHours,
                        staffKpi.getManufacturingPoint(),
                        totalManufactoringPoints,
                        uniqueProcesses.size(), staffKpi.getOleGoal(), 0f,
                        staffKpi.getKpi(), kpi, staffKpi.getMachineTimeGoal(), totalMachineTime,
                        staffKpi.getPgTimeGoal(), totalPgTime));
            }

            date = date.plusDays(days);
            map.put(date.toString(), overviewDtos);
        }
        return map;
    }

    @Override
    public ByteArrayInputStream exportExcel(GroupEfficiencyRequestDto requestDto) throws IOException {
        HashMap<String, List<StaffGroupOverviewDto>> overviewDtos = calculateDataByDay(requestDto);
        String[] headers = { "Ngày", "Tên Nhân Viên", "Mã nhân viên", };
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
            // for (int i = 1; i < data.size(); i++) {
            // Row row = sheet.createRow(i);
            // for (int j = 0; j < data.get(i).length; j++) {
            // row.createCell(j).setCellValue(data.get(i)[j]);
            // }
            // }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }

    }

}
