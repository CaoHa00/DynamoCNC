package com.example.Dynamo_Backend.service.implementation;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.TimePeriodInfo;
import com.example.Dynamo_Backend.dto.RequestDto.GroupEfficiencyRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.*;
import com.example.Dynamo_Backend.entities.DrawingCodeProcess;
import com.example.Dynamo_Backend.entities.Group;
import com.example.Dynamo_Backend.entities.Log;
import com.example.Dynamo_Backend.entities.MachineKpi;
import com.example.Dynamo_Backend.entities.ProcessTime;
import com.example.Dynamo_Backend.exception.BusinessException;
import com.example.Dynamo_Backend.mapper.MachineKpiMapper;
import com.example.Dynamo_Backend.repository.DrawingCodeProcessRepository;
import com.example.Dynamo_Backend.repository.GroupRepository;
import com.example.Dynamo_Backend.repository.LogRepository;
import com.example.Dynamo_Backend.repository.MachineKpiRepository;
import com.example.Dynamo_Backend.repository.dto.MachineRunTimeDto;
import com.example.Dynamo_Backend.service.MachineGroupStatisticService;
import com.example.Dynamo_Backend.service.ProcessTimeService;
import com.example.Dynamo_Backend.util.TimeRange;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class MachineGroupStatisticImplementation implements MachineGroupStatisticService {

    @Autowired
    private com.example.Dynamo_Backend.service.GroupEfficiencyService groupEfficiencyService;
    @Autowired
    private MachineKpiRepository machineKpiRepository;

    @Autowired
    private DrawingCodeProcessRepository processRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ProcessTimeService processTimeService;

    @Autowired
    private LogRepository logRepository;

    public MachineGroupStatisticDto calculateTotalTime(TimePeriodInfo timePeriodInfo, String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException("Group not found when get machine group statistic"));
        List<MachineKpi> machineKpiList = machineKpiRepository.findByGroup_groupIdAndMonthAndYear(
                groupId, timePeriodInfo.getMonth(), timePeriodInfo.getYear());
        if (machineKpiList.isEmpty()) {
            return new MachineGroupStatisticDto(group.getGroupId(), group.getGroupName(), 0f, 0f, 0f, 0f, 0f, 0f, 0f,
                    0f, 0f,
                    0f, 0f, 0f, machineKpiList.stream().map(MachineKpiMapper::mapToMachineDto).toList());
        }
        Float totalRunTime = 0f;
        Float totalStopTime = 0f;
        Float totalPgTime = 0f;
        Float totalOffsetTime = 0f;
        Float totalSpanTime = 0f;

        Float totalErrorTime = 0f;

        List<Integer> machineIds = machineKpiList.stream()
                .map(kpi -> kpi.getMachine().getMachineId())
                .toList();

        List<Log> allLogs = logRepository.findByMachine_machineIdInAndTimeStampBetweenOrderByTimeStampAsc(
                machineIds, timePeriodInfo.getStartDate(), timePeriodInfo.getEndDate());

        Map<Integer, List<Log>> logsByMachine = allLogs.stream()
                .collect(Collectors.groupingBy(log -> log.getMachine().getMachineId()));

        for (Integer machineId : logsByMachine.keySet()) {
            List<DrawingCodeProcess> processes = processRepository.findByMachine_MachineId(machineId);

            // Group logs by process
            for (DrawingCodeProcess process : processes) {
                if (process.getStartTime() == null || process.getEndTime() == null)
                    continue;
                // Only consider processes in the time range
                if (process.getStartTime() > timePeriodInfo.getEndDate() ||
                        process.getEndTime() < timePeriodInfo.getStartDate()) {
                    continue;
                }
                ProcessTime processTime = process.getProcessTime() == null
                        ? processTimeService.calculateProcessTime(process)
                        : process.getProcessTime();
                totalSpanTime += processTime.getSpanTime();
            }
            boolean isLast = false;
            for (int i = 0; i < logsByMachine.get(machineId).size(); i++) {
                Log log = logsByMachine.get(machineId).get(i);
                String status = log.getStatus();

                isLast = (i + 1 >= logsByMachine.get(machineId).size());
                Log next = isLast ? null : logsByMachine.get(machineId).get(i + 1);
                if (log.getStatus().contains("E")) {
                    totalErrorTime += isLast
                            ? (Math.min(timePeriodInfo.getEndDate(), System.currentTimeMillis()) - log.getTimeStamp())
                            : (next.getTimeStamp() - log.getTimeStamp());
                } else {
                    switch (status) {
                        case "R1":
                            totalPgTime += isLast
                                    ? (Math.min(timePeriodInfo.getEndDate(), System.currentTimeMillis())
                                            - log.getTimeStamp())
                                    : (next.getTimeStamp() - log.getTimeStamp());
                            break;
                        case "R2":
                            totalOffsetTime += isLast
                                    ? (Math.min(timePeriodInfo.getEndDate(), System.currentTimeMillis())
                                            - log.getTimeStamp())
                                    : (next.getTimeStamp() - log.getTimeStamp());
                            break;
                        default:
                            totalStopTime += isLast
                                    ? (Math.min(timePeriodInfo.getEndDate(), System.currentTimeMillis())
                                            - log.getTimeStamp())
                                    : (next.getTimeStamp() - log.getTimeStamp());
                            break;
                    }
                }
            }
        }
        totalRunTime = totalPgTime + totalOffsetTime;
        return new MachineGroupStatisticDto(groupId, "", totalRunTime, totalStopTime, totalPgTime,
                totalOffsetTime, totalSpanTime, totalErrorTime, totalErrorTime, 0f, 0f,
                0f, 0f, 0f, machineKpiList.stream().map(MachineKpiMapper::mapToMachineDto).toList());
    }

    @Override
    public MachineGroupStatisticDto getGroupStatistic(GroupEfficiencyRequestDto requestDto) {
        String startDate = requestDto.getStartDate().concat(" 00:00:00");
        String endDate = requestDto.getEndDate().concat(" 23:59:59");
        requestDto.setStartDate(startDate);
        requestDto.setEndDate(endDate);

        TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);
        TimePeriodInfo previousTime = TimeRange.getPreviousTimeRange(timePeriodInfo);
        Float totalRunTimeRate = 0f;
        Float totalStopTimeRate = 0f;
        Float totalPgTimeRate = 0f;
        Float totalOffsetTimeRate = 0f;
        Float totalSpanTimeRate = 0f;
        Float totalErrorTimeRate = 0f;

        MachineGroupStatisticDto currentPeriodStats = calculateTotalTime(timePeriodInfo, requestDto.getGroupId());
        MachineGroupStatisticDto previousPeriodStats = calculateTotalTime(previousTime, requestDto.getGroupId());

        List<MachineKpi> previousMachineKpiList = machineKpiRepository.findByGroup_groupIdAndMonthAndYear(
                requestDto.getGroupId(), previousTime.getMonth(), previousTime.getYear());
        Group group = groupRepository.findById(requestDto.getGroupId()).orElse(null);
        if (previousMachineKpiList.isEmpty()) {
            return new MachineGroupStatisticDto(requestDto.getGroupId(), group != null ? group.getGroupName() : "",
                    currentPeriodStats.getTotalRunTime(), currentPeriodStats.getTotalStopTime(),
                    currentPeriodStats.getTotalPgTime(), currentPeriodStats.getTotalOffsetTime(),
                    currentPeriodStats.getTotalSpanTime(), currentPeriodStats.getTotalErrorTime(), 0f, 0f, 0f, 0f, 0f,
                    0f, previousMachineKpiList.stream().map(MachineKpiMapper::mapToMachineDto).toList());
        }

        if (previousPeriodStats.getTotalOffsetTime() != 0f) {
            totalOffsetTimeRate = ((currentPeriodStats.getTotalOffsetTime() - previousPeriodStats.getTotalOffsetTime())
                    / previousPeriodStats.getTotalOffsetTime()) * 100;
        }

        if (previousPeriodStats.getTotalPgTime() != 0f) {
            totalPgTimeRate = ((currentPeriodStats.getTotalPgTime() - previousPeriodStats.getTotalPgTime())
                    / previousPeriodStats.getTotalPgTime()) * 100;
        }

        if (previousPeriodStats.getTotalRunTime() != 0f) {
            totalRunTimeRate = ((currentPeriodStats.getTotalRunTime() - previousPeriodStats.getTotalRunTime())
                    / previousPeriodStats.getTotalRunTime()) * 100;
        }

        if (previousPeriodStats.getTotalSpanTime() != 0f) {
            totalSpanTimeRate = ((currentPeriodStats.getTotalSpanTime() - previousPeriodStats.getTotalSpanTime())
                    / previousPeriodStats.getTotalSpanTime()) * 100;
        }

        if (previousPeriodStats.getTotalStopTime() != 0f) {
            totalStopTimeRate = ((currentPeriodStats.getTotalStopTime() - previousPeriodStats.getTotalStopTime())
                    / previousPeriodStats.getTotalStopTime()) * 100;
        }

        if (previousPeriodStats.getTotalErrorTime() != 0f) {
            totalErrorTimeRate = ((currentPeriodStats.getTotalErrorTime() - previousPeriodStats.getTotalErrorTime())
                    / previousPeriodStats.getTotalErrorTime()) * 100;
        }

        currentPeriodStats.setTotalErrorTime(currentPeriodStats.getTotalErrorTime() / 3600000f);
        currentPeriodStats.setTotalOffsetTime(currentPeriodStats.getTotalOffsetTime() / 3600000f);
        currentPeriodStats.setTotalPgTime(currentPeriodStats.getTotalPgTime() / 3600000f);
        currentPeriodStats.setTotalStopTime(currentPeriodStats.getTotalStopTime() / 3600000f);
        currentPeriodStats.setTotalRunTime(currentPeriodStats.getTotalRunTime() / 3600000f);
        currentPeriodStats.setTotalSpanTime(currentPeriodStats.getTotalSpanTime() / 3600000f);
        return new MachineGroupStatisticDto(group.getGroupId(), group.getGroupName(),
                currentPeriodStats.getTotalRunTime(), currentPeriodStats.getTotalStopTime(),
                currentPeriodStats.getTotalPgTime(), currentPeriodStats.getTotalOffsetTime(),
                currentPeriodStats.getTotalSpanTime(), currentPeriodStats.getTotalErrorTime(), totalErrorTimeRate,
                totalRunTimeRate,
                totalStopTimeRate, totalPgTimeRate, totalOffsetTimeRate, totalSpanTimeRate,
                previousMachineKpiList.stream().map(MachineKpiMapper::mapToMachineDto).toList());
    }

    @Override
    public List<MachineGroupOverviewDto> getGroupOverview(GroupEfficiencyRequestDto requestDto) {
        String startDate = requestDto.getStartDate().concat(" 00:00:00");
        String endDate = requestDto.getEndDate().concat(" 23:59:59");
        requestDto.setStartDate(startDate);
        requestDto.setEndDate(endDate);

        TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);
        List<MachineKpi> machineKpiList = machineKpiRepository.findByGroup_groupIdAndMonthAndYear(
                requestDto.getGroupId(), timePeriodInfo.getMonth(), timePeriodInfo.getYear());
        if (machineKpiList.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> machineIds = machineKpiList.stream()
                .map(kpi -> kpi.getMachine().getMachineId())
                .toList();

        List<Log> allLogs = logRepository.findByMachine_machineIdInAndTimeStampBetweenOrderByTimeStampAsc(
                machineIds, timePeriodInfo.getStartDate(), timePeriodInfo.getEndDate());

        Map<Integer, List<Log>> logsByMachine = allLogs.stream()
                .collect(Collectors.groupingBy(log -> log.getMachine().getMachineId()));

        List<MachineGroupOverviewDto> overviewList = new ArrayList<>();

        for (Integer machineId : logsByMachine.keySet()) {
            Float totalRunTime = 0f;
            Float totalStopTime = 0f;
            Float totalPgTime = 0f;
            Float totalOffsetTime = 0f;
            Float totalSpanTime = 0f;
            Float totalErrorTime = 0f;
            Float pgTimeExpected = 0f;
            Integer doneProcesssCount = 0;

            List<Log> logs = logsByMachine.get(machineId);
            List<DrawingCodeProcess> processes = processRepository.findByMachine_MachineId(machineId);
            MachineGroupOverviewDto overviewDto = new MachineGroupOverviewDto();
            overviewDto.setMachineId(machineId);
            overviewDto.setMachineName(logs.get(0).getMachine().getMachineName());

            if (!processes.isEmpty()) {
                for (DrawingCodeProcess process : processes) {
                    if (process.getStartTime() == null || process.getEndTime() == null)
                        continue;
                    // Only consider processes in the time range
                    if (process.getStartTime() > timePeriodInfo.getEndDate() ||
                            process.getEndTime() < timePeriodInfo.getStartDate()) {
                        continue;
                    }
                    if (process.getProcessStatus() == 3) {
                        doneProcesssCount++;
                    }
                    ProcessTime processTime = process.getProcessTime() == null
                            ? processTimeService.calculateProcessTime(process)
                            : process.getProcessTime();
                    totalSpanTime += processTime.getSpanTime();
                    pgTimeExpected += process.getPgTime();
                }
            }
            overviewDto.setNumberOfProcesses(doneProcesssCount);

            boolean isLast = false;
            for (int i = 0; i < logsByMachine.get(machineId).size(); i++) {
                Log log = logsByMachine.get(machineId).get(i);
                String status = log.getStatus();

                isLast = (i + 1 >= logsByMachine.get(machineId).size());
                Log next = isLast ? null : logsByMachine.get(machineId).get(i + 1);
                if (log.getStatus().contains("E")) {
                    totalErrorTime += isLast
                            ? (Math.min(timePeriodInfo.getEndDate(), System.currentTimeMillis()) - log.getTimeStamp())
                            : (next.getTimeStamp() - log.getTimeStamp());
                }
                switch (status) {
                    case "R1":
                        totalPgTime += isLast
                                ? (Math.min(timePeriodInfo.getEndDate(), System.currentTimeMillis())
                                        - log.getTimeStamp())
                                : (next.getTimeStamp() - log.getTimeStamp());
                        break;
                    case "R2":
                        totalOffsetTime += isLast
                                ? (Math.min(timePeriodInfo.getEndDate(), System.currentTimeMillis())
                                        - log.getTimeStamp())
                                : (next.getTimeStamp() - log.getTimeStamp());
                        break;
                    default:
                        totalStopTime += isLast
                                ? (Math.min(timePeriodInfo.getEndDate(), System.currentTimeMillis())
                                        - log.getTimeStamp())
                                : (next.getTimeStamp() - log.getTimeStamp());
                        break;
                }
            }

            totalRunTime = totalPgTime + totalOffsetTime;
            overviewDto.setRunTime(totalRunTime / 3600000f);
            overviewDto.setStopTime(totalStopTime / 3600000f);
            overviewDto.setPgTime(totalPgTime / 3600000f);
            overviewDto.setOffsetTime(totalOffsetTime / 3600000f);
            overviewDto.setSpanTime(totalSpanTime / 3600000f);
            overviewDto.setPgTimeExpect(pgTimeExpected);
            overviewList.add(overviewDto);
        }
        return overviewList;
    }

    @Override
    public TotalRunTimeResponse getTotalRunTime(GroupEfficiencyRequestDto requestDto) {
        Float totalRunTimeMainProduct = 0f;
        Float runTimeOfRerun = 0f;
        Float runTimeOfLK = 0f;
        Float runTimeOfElectric = 0f;
        Float totalRunTimeOfPreparation = 0f;
        Float totalPgTime = 0f;
        Float totalOffsetTime = 0f;
        Float totalStopTime = 0f;
        Float totalErrorTime = 0f;

        String startDate = requestDto.getStartDate().concat(" 00:00:00");
        String endDate = requestDto.getEndDate().concat(" 23:59:59");
        requestDto.setStartDate(startDate);
        requestDto.setEndDate(endDate);
        TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);

        List<MachineKpi> machineKpiList = machineKpiRepository.findByGroup_groupIdAndMonthAndYear(
                requestDto.getGroupId(), timePeriodInfo.getMonth(), timePeriodInfo.getYear());
        if (machineKpiList.isEmpty()) {
            return new TotalRunTimeResponse(totalRunTimeMainProduct, runTimeOfRerun, runTimeOfLK, runTimeOfElectric,
                    totalRunTimeOfPreparation, totalPgTime, totalOffsetTime, totalStopTime, totalErrorTime);
        }
        for (MachineKpi machineKpi : machineKpiList) {

            List<DrawingCodeProcess> drawingCodeProcesses = processRepository
                    .findProcessesByMachineInRange(machineKpi.getMachine().getMachineId(),
                            timePeriodInfo.getStartDate(), timePeriodInfo.getEndDate());
            if (drawingCodeProcesses != null && !drawingCodeProcesses.isEmpty()) {
                for (DrawingCodeProcess process : drawingCodeProcesses) {
                    ProcessTime processTime = process.getProcessTime();
                    if (processTime == null) {
                        processTime = processTimeService.calculateProcessTime(process);
                    }
                    if (process.getProcessType().equals("SP_Chính")) {
                        totalRunTimeMainProduct += processTime.getRunTime();
                    }
                    if (process.getProcessType().contains("NG")) {
                        runTimeOfRerun += processTime.getRunTime();
                    }
                    if (process.getProcessType().contains("LK")) {
                        runTimeOfLK += processTime.getRunTime();
                    }
                    if (process.getProcessType().equals("Điện cực")) {
                        runTimeOfElectric += processTime.getRunTime();
                    }
                    if (process.getProcessType().equals("Dự bị")) {
                        totalRunTimeOfPreparation += processTime.getRunTime();
                    }
                }
            }

            List<Log> logs = logRepository.findByMachine_machineIdAndTimeStampBetweenOrderByTimeStampAsc(
                    machineKpi.getMachine().getMachineId(), timePeriodInfo.getStartDate(), timePeriodInfo.getEndDate());
            if (logs == null || logs.isEmpty()) {
                continue;
            }
            for (int i = 0; i < logs.size(); i++) {
                Log log = logs.get(i);
                String status = log.getStatus();

                if (i + 1 >= logs.size())
                    break;
                Log next = logs.get(i + 1);
                if (log.getStatus().contains("E")) {
                    totalErrorTime += (next.getTimeStamp() - log.getTimeStamp());
                }
                switch (status) {
                    case "R1":
                        totalPgTime += (next.getTimeStamp() - log.getTimeStamp());
                        break;
                    case "R2":
                        totalOffsetTime += (next.getTimeStamp() - log.getTimeStamp());
                        break;
                    default:
                        totalStopTime += (next.getTimeStamp() - log.getTimeStamp());
                        break;
                }
            }
            totalErrorTime /= 3600000f;
            totalPgTime /= 3600000f;
            totalOffsetTime /= 3600000f;
            totalStopTime /= 3600000f;
        }
        return new TotalRunTimeResponse(totalRunTimeMainProduct, runTimeOfRerun, runTimeOfLK, runTimeOfElectric,
                totalRunTimeOfPreparation, totalPgTime, totalOffsetTime, totalStopTime, totalErrorTime);
    }

    @Override
    public List<MachineRunTimeDto> getTop5GroupOverview(GroupEfficiencyRequestDto requestDto) {
        String startDate = requestDto.getStartDate().concat(" 00:00:00");
        String endDate = requestDto.getEndDate().concat(" 23:59:59");
        requestDto.setStartDate(startDate);
        requestDto.setEndDate(endDate);
        TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);

        List<MachineKpi> machineKpiList = machineKpiRepository.findByGroup_groupIdAndMonthAndYear(
                requestDto.getGroupId(), timePeriodInfo.getMonth(), timePeriodInfo.getYear());
        if (machineKpiList == null || machineKpiList.isEmpty()) {
            return List.of();
        }
        List<MachineRunTimeDto> top5MachineRunTime = logRepository.findTop5MachineRunTimeByGroupAndTime(
                requestDto.getGroupId(), timePeriodInfo.getMonth(), timePeriodInfo.getYear(),
                timePeriodInfo.getStartDate(), timePeriodInfo.getEndDate());
        return top5MachineRunTime;
    }

    @Override
    public void exportExcel(GroupEfficiencyRequestDto requestDto) {
        String fileName = "";
        Group group = groupRepository.findById(requestDto.getGroupId())
                .orElseThrow(() -> new BusinessException("Group not found when export machine group statistic"));
        TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Thống kê nhóm máy");
        int rowIdx = 0;
        Row headerRow = sheet.createRow(rowIdx++);
        headerRow.createCell(0).setCellValue("Period");
        headerRow.createCell(1).setCellValue("Total Run Time (h)");
        headerRow.createCell(2).setCellValue("Total Run Time Main Product (h)");
        headerRow.createCell(3).setCellValue("Run Time Of Rerun (h)");
        headerRow.createCell(4).setCellValue("Run Time Of LK (h)");
        headerRow.createCell(5).setCellValue("Run Time Of Electric (h)");
        headerRow.createCell(6).setCellValue("Total Run Time Of Preparation (h)");
        headerRow.createCell(7).setCellValue("Total Pg Time (h)");
        headerRow.createCell(8).setCellValue("Total Offset Time (h)");
        headerRow.createCell(9).setCellValue("Total Stop Time (h)");
        headerRow.createCell(10).setCellValue("Total Error Time (h)");
        headerRow.createCell(11).setCellValue("Operational Efficiency");
        headerRow.createCell(12).setCellValue("PG Efficiency");
        headerRow.createCell(13).setCellValue("Value Efficiency");
        headerRow.createCell(14).setCellValue("OEE");
        headerRow.createCell(15).setCellValue("Offset Loss");
        headerRow.createCell(16).setCellValue("Other Loss");

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        if (timePeriodInfo.isMonth()) {
            fileName = "Thống kê nhóm " + group.getGroupName() + " tháng " + timePeriodInfo.getMonth() + "-"
                    + timePeriodInfo.getYear() + ".xlsx";
            for (int week = 1; week <= 4; week++) {
                TimePeriodInfo weekInfo = buildWeekTimePeriodInfo(timePeriodInfo, week);
                if (weekInfo == null)
                    continue;
                GroupEfficiencyRequestDto weekDto = new GroupEfficiencyRequestDto();
                weekDto.setGroupId(requestDto.getGroupId());
                weekDto.setStartDate(Instant.ofEpochMilli(weekInfo.getStartDate()).atZone(ZoneId.systemDefault())
                        .toLocalDate().format(dateFormatter));
                weekDto.setEndDate(Instant.ofEpochMilli(weekInfo.getEndDate()).atZone(ZoneId.systemDefault())
                        .toLocalDate().format(dateFormatter));
                TotalRunTimeResponse stats = getTotalRunTime(weekDto);
                com.example.Dynamo_Backend.dto.ResponseDto.GroupEfficiencyResponseDto eff = groupEfficiencyService
                        .getGroupEfficiency(weekDto);
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue("Week " + week);
                row.createCell(1).setCellValue(stats.getRunTimeOfMainProduct() + stats.getRunTimeOfRerun()
                        + stats.getRunTimeOfLK() + stats.getRunTimeOfElectric()
                        + stats.getTotalRunTimeOfPreparation());
                row.createCell(2).setCellValue(stats.getRunTimeOfMainProduct());
                row.createCell(3).setCellValue(stats.getRunTimeOfRerun());
                row.createCell(4).setCellValue(stats.getRunTimeOfLK());
                row.createCell(5).setCellValue(stats.getRunTimeOfElectric());
                row.createCell(6).setCellValue(stats.getTotalRunTimeOfPreparation());
                row.createCell(7).setCellValue(stats.getTotalPgTime());
                row.createCell(8).setCellValue(stats.getTotalOffsetTime());
                row.createCell(9).setCellValue(stats.getTotalStopTime());
                row.createCell(10).setCellValue(stats.getTotalErrorTime());
                row.createCell(11).setCellValue(eff.getOperationalEfficiency());
                row.createCell(12).setCellValue(eff.getPgEfficiency());
                row.createCell(13).setCellValue(eff.getValueEfficiency());
                row.createCell(14).setCellValue(eff.getOee());
                row.createCell(15).setCellValue(eff.getOffsetLoss());
                row.createCell(16).setCellValue(eff.getOtherLoss());
            }
        } else if (timePeriodInfo.getDay() <= 7) {
            fileName = "Thống kê nhóm " + group.getGroupName() + " "
                    + Instant.ofEpochMilli(timePeriodInfo.getStartDate())
                            .atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter)
                    + " - "
                    + Instant.ofEpochMilli(timePeriodInfo.getEndDate())
                            .atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter)
                    + ".xlsx";
            long days = timePeriodInfo.getDay();
            LocalDate start = Instant.ofEpochMilli(timePeriodInfo.getStartDate())
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            for (int i = 0; i < days; i++) {
                LocalDate day = start.plusDays(i);
                long startMillis = day.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
                long endMillis = day.atTime(23, 59, 59).atZone(java.time.ZoneId.systemDefault()).toInstant()
                        .toEpochMilli();
                GroupEfficiencyRequestDto dayDto = new GroupEfficiencyRequestDto();
                dayDto.setGroupId(requestDto.getGroupId());
                dayDto.setStartDate(day.format(dateFormatter));
                dayDto.setEndDate(day.format(dateFormatter));
                TotalRunTimeResponse stats = getTotalRunTime(dayDto);
                com.example.Dynamo_Backend.dto.ResponseDto.GroupEfficiencyResponseDto eff = groupEfficiencyService
                        .getGroupEfficiency(dayDto);
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(day.format(dateFormatter));
                row.createCell(1).setCellValue(stats.getRunTimeOfMainProduct() + stats.getRunTimeOfRerun()
                        + stats.getRunTimeOfLK() + stats.getRunTimeOfElectric()
                        + stats.getTotalRunTimeOfPreparation());
                row.createCell(2).setCellValue(stats.getRunTimeOfMainProduct());
                row.createCell(3).setCellValue(stats.getRunTimeOfRerun());
                row.createCell(4).setCellValue(stats.getRunTimeOfLK());
                row.createCell(5).setCellValue(stats.getRunTimeOfElectric());
                row.createCell(6).setCellValue(stats.getTotalRunTimeOfPreparation());
                row.createCell(7).setCellValue(stats.getTotalPgTime());
                row.createCell(8).setCellValue(stats.getTotalOffsetTime());
                row.createCell(9).setCellValue(stats.getTotalStopTime());
                row.createCell(10).setCellValue(stats.getTotalErrorTime());
                row.createCell(11).setCellValue(eff.getOperationalEfficiency());
                row.createCell(12).setCellValue(eff.getPgEfficiency());
                row.createCell(13).setCellValue(eff.getValueEfficiency());
                row.createCell(14).setCellValue(eff.getOee());
                row.createCell(15).setCellValue(eff.getOffsetLoss());
                row.createCell(16).setCellValue(eff.getOtherLoss());
            }
        }
        try (java.io.FileOutputStream fileOut = new java.io.FileOutputStream(fileName)) {
            workbook.write(fileOut);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (Exception ignore) {
            }
        }
        // In production, you may want to write to HttpServletResponse for download
    }

    // Helper to build TimePeriodInfo for a week in a month
    private TimePeriodInfo buildWeekTimePeriodInfo(TimePeriodInfo monthInfo, int week) {
        LocalDate firstDay = Instant.ofEpochMilli(monthInfo.getStartDate())
                .atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate lastDay = Instant.ofEpochMilli(monthInfo.getEndDate())
                .atZone(ZoneId.systemDefault()).toLocalDate();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate weekStart = null, weekEnd = null;
        for (LocalDate d = firstDay; !d.isAfter(lastDay); d = d.plusDays(1)) {
            int weekOfMonth = d.get(weekFields.weekOfMonth());
            if (weekOfMonth == week) {
                if (weekStart == null)
                    weekStart = d;
                weekEnd = d;
            }
        }
        if (weekStart == null || weekEnd == null)
            return null;
        long startMillis = weekStart.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endMillis = weekEnd.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return new TimePeriodInfo(false, week, monthInfo.getMonth(), monthInfo.getYear(),
                (long) (weekEnd.toEpochDay() - weekStart.toEpochDay() + 1), startMillis, endMillis);
    }

    @Override
    public void exportExcelToResponse(GroupEfficiencyRequestDto requestDto,
            HttpServletResponse response) {
        String fileName = "Data.xlsx";
        String title = "";
        String startDate = requestDto.getStartDate().concat(" 00:00:00");
        String endDate = requestDto.getEndDate().concat(" 23:59:59");
        requestDto.setStartDate(startDate);
        requestDto.setEndDate(endDate);
        Group group = groupRepository.findById(requestDto.getGroupId())
                .orElseThrow(() -> new BusinessException("Group not found when export machine group statistic"));
        TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Thống kê nhóm máy");
        int rowIdx = 5;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter exportDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Row headerRow = sheet.createRow(rowIdx++);
        headerRow.createCell(0).setCellValue("Thời gian");
        headerRow.createCell(1).setCellValue("Giờ chạy");
        headerRow.createCell(2).setCellValue("Giờ chạy SP chính ");
        headerRow.createCell(3).setCellValue("Giờ chạy NG_Chạy lại");
        headerRow.createCell(4).setCellValue("Giờ chạy LK đồ gá");
        headerRow.createCell(5).setCellValue("Giờ chạy điện cực");
        headerRow.createCell(6).setCellValue("Giờ chạy dự bị");
        headerRow.createCell(7).setCellValue("Giờ chạy PG ");
        headerRow.createCell(8).setCellValue("Giờ chạy Offset ");
        headerRow.createCell(9).setCellValue("Giờ chạy Dừng ");
        headerRow.createCell(10).setCellValue("Giờ chạy Lỗi ");
        headerRow.createCell(11).setCellValue("Hiệu suất vận hành");
        headerRow.createCell(12).setCellValue("Hiệu suất PG");
        headerRow.createCell(13).setCellValue("Hiệu suất Giá trị");
        headerRow.createCell(14).setCellValue("OEE");
        headerRow.createCell(15).setCellValue("Tổn thất Offset");
        headerRow.createCell(16).setCellValue("Tổn thất khác");

        if (timePeriodInfo.isMonth()) {
            title = "Thống kê nhóm " + group.getGroupName() + " tháng " +
                    timePeriodInfo.getMonth() + "-"
                    + timePeriodInfo.getYear() + ".xlsx";
            for (int week = 1; week <= 4; week++) {
                TimePeriodInfo weekInfo = buildWeekTimePeriodInfo(timePeriodInfo, week);
                if (weekInfo == null)
                    continue;
                GroupEfficiencyRequestDto weekDto = new GroupEfficiencyRequestDto();
                weekDto.setGroupId(requestDto.getGroupId());
                weekDto.setStartDate(Instant.ofEpochMilli(weekInfo.getStartDate())
                        .atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter));
                weekDto.setEndDate(Instant.ofEpochMilli(weekInfo.getEndDate())
                        .atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter));
                TotalRunTimeResponse stats = getTotalRunTime(weekDto);
                GroupEfficiencyResponseDto eff = groupEfficiencyService.getGroupEfficiency(weekDto);
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue("Week " + week);
                row.createCell(1).setCellValue(stats.getRunTimeOfMainProduct() + stats.getRunTimeOfRerun()
                        + stats.getRunTimeOfLK() + stats.getRunTimeOfElectric()
                        + stats.getTotalRunTimeOfPreparation());
                row.createCell(2).setCellValue(stats.getRunTimeOfMainProduct());
                row.createCell(3).setCellValue(stats.getRunTimeOfRerun());
                row.createCell(4).setCellValue(stats.getRunTimeOfLK());
                row.createCell(5).setCellValue(stats.getRunTimeOfElectric());
                row.createCell(6).setCellValue(stats.getTotalRunTimeOfPreparation());
                row.createCell(7).setCellValue(stats.getTotalPgTime());
                row.createCell(8).setCellValue(stats.getTotalOffsetTime());
                row.createCell(9).setCellValue(stats.getTotalStopTime());
                row.createCell(10).setCellValue(stats.getTotalErrorTime());
                row.createCell(11).setCellValue(eff.getOperationalEfficiency());
                row.createCell(12).setCellValue(eff.getPgEfficiency());
                row.createCell(13).setCellValue(eff.getValueEfficiency());
                row.createCell(14).setCellValue(eff.getOee());
                row.createCell(15).setCellValue(eff.getOffsetLoss());
                row.createCell(16).setCellValue(eff.getOtherLoss());
            }
        } else if (timePeriodInfo.getDay() <= 7) {
            title = "Thống kê nhóm " + group.getGroupName() + " "
                    + Instant.ofEpochMilli(timePeriodInfo.getStartDate())
                            .atZone(ZoneId.systemDefault()).toLocalDate().format(exportDateFormatter)
                    + " - "
                    + Instant.ofEpochMilli(timePeriodInfo.getEndDate())
                            .atZone(ZoneId.systemDefault()).toLocalDate().format(exportDateFormatter)
                    + ".xlsx";
            long days = timePeriodInfo.getDay();
            LocalDate start = Instant.ofEpochMilli(timePeriodInfo.getStartDate())
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            for (int i = 0; i < days; i++) {
                LocalDate day = start.plusDays(i);
                GroupEfficiencyRequestDto dayDto = new GroupEfficiencyRequestDto();
                dayDto.setGroupId(requestDto.getGroupId());
                dayDto.setStartDate(day.format(dateFormatter));
                dayDto.setEndDate(day.format(dateFormatter));
                GroupEfficiencyRequestDto dayDtotemp = new GroupEfficiencyRequestDto();
                dayDtotemp.setGroupId(requestDto.getGroupId());
                dayDtotemp.setStartDate(day.format(dateFormatter));
                dayDtotemp.setEndDate(day.format(dateFormatter));
                TotalRunTimeResponse stats = getTotalRunTime(dayDto);
                GroupEfficiencyResponseDto eff = groupEfficiencyService.getGroupEfficiency(dayDtotemp);
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(day.format(dateFormatter));
                row.createCell(1).setCellValue(stats.getRunTimeOfMainProduct() + stats.getRunTimeOfRerun()
                        + stats.getRunTimeOfLK() + stats.getRunTimeOfElectric()
                        + stats.getTotalRunTimeOfPreparation());
                row.createCell(2).setCellValue(stats.getRunTimeOfMainProduct());
                row.createCell(3).setCellValue(stats.getRunTimeOfRerun());
                row.createCell(4).setCellValue(stats.getRunTimeOfLK());
                row.createCell(5).setCellValue(stats.getRunTimeOfElectric());
                row.createCell(6).setCellValue(stats.getTotalRunTimeOfPreparation());
                row.createCell(7).setCellValue(stats.getTotalPgTime());
                row.createCell(8).setCellValue(stats.getTotalOffsetTime());
                row.createCell(9).setCellValue(stats.getTotalStopTime());
                row.createCell(10).setCellValue(stats.getTotalErrorTime());
                row.createCell(11).setCellValue(eff.getOperationalEfficiency());
                row.createCell(12).setCellValue(eff.getPgEfficiency());
                row.createCell(13).setCellValue(eff.getValueEfficiency());
                row.createCell(14).setCellValue(eff.getOee());
                row.createCell(15).setCellValue(eff.getOffsetLoss());
                row.createCell(16).setCellValue(eff.getOtherLoss());
            }
        }
        Row titleRow = sheet.createRow(1);
        titleRow.createCell(6).setCellValue(title.replace(".xlsx", ""));
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            workbook.write(response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (Exception ignore) {
            }
        }
    }
}
