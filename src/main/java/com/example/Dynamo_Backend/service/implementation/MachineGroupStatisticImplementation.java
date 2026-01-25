package com.example.Dynamo_Backend.service.implementation;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.MachineDailySummaryByMachine;
import com.example.Dynamo_Backend.dto.MachineRunTimeDto;
import com.example.Dynamo_Backend.dto.TimePeriodInfo;
import com.example.Dynamo_Backend.dto.RequestDto.GroupEfficiencyRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.*;
import com.example.Dynamo_Backend.entities.DrawingCodeProcess;
import com.example.Dynamo_Backend.entities.Group;
import com.example.Dynamo_Backend.entities.GroupKpi;
import com.example.Dynamo_Backend.entities.Log;
import com.example.Dynamo_Backend.entities.MachineKpi;
import com.example.Dynamo_Backend.entities.ProcessTime;
import com.example.Dynamo_Backend.exception.BusinessException;
import com.example.Dynamo_Backend.mapper.MachineKpiMapper;
import com.example.Dynamo_Backend.repository.*;
import com.example.Dynamo_Backend.service.MachineGroupStatisticService;
import com.example.Dynamo_Backend.service.ProcessTimeService;
import com.example.Dynamo_Backend.util.DateTimeUtil;
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

        @Autowired
        private MachineRepository machineRepository;

        @Autowired
        private GroupKpiRepository groupKpiRepository;

        @Autowired
        private MachineDailyRepository machineDailyRepository;

        // checked
        public MachineGroupStatisticDto calculateTotalTime(TimePeriodInfo timePeriodInfo, String groupId,
                        String shiftCode) {
                Group group = groupRepository.findById(groupId)
                                .orElseThrow(() -> new BusinessException(
                                                "Group not found when get machine group statistic"));
                List<MachineKpi> machineKpiList = machineKpiRepository.findByGroup_groupIdAndMonthAndYear(
                                groupId, timePeriodInfo.getMonth(), timePeriodInfo.getYear());
                if (machineKpiList.isEmpty()) {
                        return new MachineGroupStatisticDto(group.getGroupId(), group.getGroupName(), 0f, 0f, 0f, 0f,
                                        0f, 0f, 0f,
                                        0f, 0f,
                                        0f, 0f, 0f, 0, 0f, 0,
                                        machineKpiList.stream().map(MachineKpiMapper::mapToMachineDto).toList());
                }
                Float totalRunTime = 0f;
                Float totalStopTime = 0f;
                Float totalPgTime = 0f;
                Float totalOffsetTime = 0f;
                Float totalSpanTime = 0f;
                Float totalErrorTime = 0f;
                Integer totalProcesses = 0;

                List<Integer> machineIds = machineKpiList.stream()
                                .map(kpi -> kpi.getMachine().getMachineId())
                                .toList();

                MachineDailySummaryByMachine sum = machineDailyRepository.sumByMachines(machineIds,
                                timePeriodInfo.getStart(), timePeriodInfo.getEnd(), shiftCode);

                // List<Log> allLogs =
                // logRepository.findByMachine_machineIdInAndTimeStampBetweenOrderByTimeStampAsc(
                // machineIds, timePeriodInfo.getStartDate(), timePeriodInfo.getEndDate());

                // Map<Integer, List<Log>> logsByMachine = allLogs.stream()
                // .collect(Collectors.groupingBy(log -> log.getMachine().getMachineId()));

                // for (Integer machineId : logsByMachine.keySet()) {
                // List<DrawingCodeProcess> processes =
                // processRepository.findByMachine_MachineIdAndStatus(machineId, 1);

                // // Group logs by process
                // for (DrawingCodeProcess process : processes) {
                // if (process.getStartTime() == null || process.getEndTime() == null)
                // continue;
                // // Only consider processes in the time range
                // if (process.getStartTime() > timePeriodInfo.getEndDate() ||
                // process.getEndTime() < timePeriodInfo.getStartDate()) {
                // continue;
                // }
                // if (process.getProcessStatus() == 3) {
                // totalProcesses++;
                // }
                // ProcessTime processTime = process.getProcessTime() == null
                // ? processTimeService.calculateProcessTime(process)
                // : process.getProcessTime();
                // totalSpanTime += processTime.getSpanTime();
                // }

                // List<Float> activeTime =
                // machineRepository.calculateDurationsByStatusAndRange(
                // machineId, startDate,
                // endDate);
                // totalRunTime += activeTime.get(3) + activeTime.get(4);
                // totalPgTime += activeTime.get(3);
                // totalStopTime += activeTime.get(5) + activeTime.get(6);
                // // totalEmptyTime = activeTime.get(0);
                // totalErrorTime += activeTime.get(1) + activeTime.get(2);
                // totalOffsetTime += activeTime.get(4);

                // }
                Long value = sum.quantity();
                if (value != null && value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
                        totalProcesses = value.intValue();
                }
                totalPgTime = sum.runPgSeconds() / 3600f;
                totalOffsetTime = sum.runOffsetSeconds() / 3600f;
                totalRunTime = totalPgTime + totalOffsetTime;
                totalStopTime = sum.stopSeconds() / 3600f;
                totalErrorTime = sum.errorSeconds() / 3600f;
                totalSpanTime = sum.spanSeconds() / 3600f;

                return new MachineGroupStatisticDto(groupId, "", totalRunTime, totalStopTime, totalPgTime,
                                totalOffsetTime, totalSpanTime, totalErrorTime, totalErrorTime, 0f, 0f,
                                0f, 0f, 0f, totalProcesses, 0f, machineKpiList.size(),
                                machineKpiList.stream().map(MachineKpiMapper::mapToMachineDto).toList());
        }

        // checked
        @Override
        public MachineGroupStatisticDto getGroupStatistic(GroupEfficiencyRequestDto requestDto) {

                TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);
                TimePeriodInfo previousTime = TimeRange.getPreviousTimeRange(timePeriodInfo);
                Float totalRunTimeRate = 0f;
                Float totalStopTimeRate = 0f;
                Float totalPgTimeRate = 0f;
                Float totalOffsetTimeRate = 0f;
                Float totalSpanTimeRate = 0f;
                Float totalErrorTimeRate = 0f;

                MachineGroupStatisticDto currentPeriodStats = calculateTotalTime(timePeriodInfo,
                                requestDto.getGroupId(),
                                requestDto.getShiftCode());

                MachineGroupStatisticDto previousPeriodStats = calculateTotalTime(previousTime, requestDto.getGroupId(),
                                requestDto.getShiftCode());

                Group group = groupRepository.findById(requestDto.getGroupId()).orElse(null);

                if (previousPeriodStats.getTotalOffsetTime() != 0f) {
                        totalOffsetTimeRate = ((currentPeriodStats.getTotalOffsetTime()
                                        - previousPeriodStats.getTotalOffsetTime())
                                        / previousPeriodStats.getTotalOffsetTime()) * 100;
                }

                if (previousPeriodStats.getTotalPgTime() != 0f) {
                        totalPgTimeRate = ((currentPeriodStats.getTotalPgTime() - previousPeriodStats.getTotalPgTime())
                                        / previousPeriodStats.getTotalPgTime()) * 100;
                }

                if (previousPeriodStats.getTotalRunTime() != 0f) {
                        totalRunTimeRate = ((currentPeriodStats.getTotalRunTime()
                                        - previousPeriodStats.getTotalRunTime())
                                        / previousPeriodStats.getTotalRunTime()) * 100;
                }

                if (previousPeriodStats.getTotalSpanTime() != 0f) {
                        totalSpanTimeRate = ((currentPeriodStats.getTotalSpanTime()
                                        - previousPeriodStats.getTotalSpanTime())
                                        / previousPeriodStats.getTotalSpanTime()) * 100;
                }

                if (previousPeriodStats.getTotalStopTime() != 0f) {
                        totalStopTimeRate = ((currentPeriodStats.getTotalStopTime()
                                        - previousPeriodStats.getTotalStopTime())
                                        / previousPeriodStats.getTotalStopTime()) * 100;
                }

                if (previousPeriodStats.getTotalErrorTime() != 0f) {
                        totalErrorTimeRate = ((currentPeriodStats.getTotalErrorTime()
                                        - previousPeriodStats.getTotalErrorTime())
                                        / previousPeriodStats.getTotalErrorTime()) * 100;
                }
                if (previousPeriodStats.getTotalProcesses() != 0) {
                        currentPeriodStats.setProcessRate(
                                        ((float) (currentPeriodStats.getTotalProcesses()
                                                        - previousPeriodStats.getTotalProcesses())
                                                        / previousPeriodStats.getTotalProcesses()) * 100);
                }

                currentPeriodStats.setTotalErrorTime(currentPeriodStats.getTotalErrorTime());
                currentPeriodStats.setTotalOffsetTime(currentPeriodStats.getTotalOffsetTime());
                currentPeriodStats.setTotalPgTime(currentPeriodStats.getTotalPgTime());
                currentPeriodStats.setTotalStopTime(currentPeriodStats.getTotalStopTime());
                currentPeriodStats.setTotalRunTime(currentPeriodStats.getTotalRunTime());
                currentPeriodStats.setTotalSpanTime(currentPeriodStats.getTotalSpanTime());
                return new MachineGroupStatisticDto(group.getGroupId(), group.getGroupName(),
                                currentPeriodStats.getTotalRunTime(), currentPeriodStats.getTotalStopTime(),
                                currentPeriodStats.getTotalPgTime(), currentPeriodStats.getTotalOffsetTime(),
                                currentPeriodStats.getTotalSpanTime(), currentPeriodStats.getTotalErrorTime(),
                                (float) Math.round(totalErrorTimeRate * 100) / 100,
                                (float) Math.round(totalRunTimeRate * 100) / 100,
                                (float) Math.round(totalStopTimeRate * 100) / 100,
                                (float) Math.round(totalPgTimeRate * 100) / 100,
                                (float) Math.round(totalOffsetTimeRate * 100) / 100,
                                (float) Math.round(totalSpanTimeRate * 100) / 100,
                                currentPeriodStats.getTotalProcesses(),
                                (float) Math.round(currentPeriodStats.getProcessRate() * 100) / 100,
                                currentPeriodStats.getMachines().size(), currentPeriodStats.getMachines());
        }

        // checked
        @Override
        public List<MachineGroupOverviewDto> getGroupOverview(GroupEfficiencyRequestDto requestDto) {
                TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);
                List<MachineKpi> machineKpiList = machineKpiRepository.findByGroup_groupIdAndMonthAndYear(
                                requestDto.getGroupId(), timePeriodInfo.getMonth(), timePeriodInfo.getYear());
                if (machineKpiList.isEmpty()) {
                        return new ArrayList<>();
                }

                GroupKpi groupKpi;
                float workingHourGoal = 0f;
                if (timePeriodInfo.isMonth()) {
                        groupKpi = groupKpiRepository.findByGroup_GroupIdAndIsMonthAndMonthAndYear(
                                        requestDto.getGroupId(), 1, timePeriodInfo.getMonth(), timePeriodInfo.getYear())
                                        .orElseGet(GroupKpi::new);
                        workingHourGoal = groupKpi.getWorkingHourGoal();
                } else {
                        groupKpi = groupKpiRepository.findByGroup_GroupIdAndWeekAndYear(
                                        requestDto.getGroupId(), timePeriodInfo.getWeekOfYear(),
                                        timePeriodInfo.getYear()).orElseGet(GroupKpi::new);
                        if (timePeriodInfo.getDay() == 1) {
                                workingHourGoal = groupKpi.getWorkingHourGoal() / 7;
                        }
                }

                List<MachineGroupOverviewDto> overviewList = new ArrayList<>();

                for (MachineKpi machine : machineKpiList) {
                        Integer machineId = machine.getMachine().getMachineId();
                        Float totalRunTime = 0f;
                        Float totalStopTime = 0f;
                        Float totalPgTime = 0f;
                        Float totalEmptyTime = 0f;
                        Float totalErrorTime = 0f;
                        Float pgTimeExpected = 0f;
                        Integer doneProcesssCount = 0;
                        List<Integer> machineIds = new ArrayList<>();
                        machineIds.add(machineId);
                        MachineDailySummaryByMachine sum = machineDailyRepository.sumByMachines(machineIds,
                                        timePeriodInfo.getStart(), timePeriodInfo.getEnd(), requestDto.getShiftCode());

                        totalPgTime = sum.runPgSeconds() / 3600f;
                        Float offset = sum.runOffsetSeconds() / 3600f;

                        totalRunTime = totalPgTime + offset;

                        totalStopTime = sum.stopSeconds() / 3600f;
                        totalEmptyTime = sum.emptySeconds() / 3600f;
                        totalErrorTime = sum.errorSeconds() / 3600f;
                        Long value = sum.quantity();
                        if (value != null && value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
                                doneProcesssCount = value.intValue();
                        }
                        pgTimeExpected = sum.expectedSeconds() / 3600f;

                        // List<DrawingCodeProcess> processes =
                        // processRepository.findByMachine_MachineIdAndStatus(machineId, 1);
                        MachineGroupOverviewDto overviewDto = new MachineGroupOverviewDto();
                        overviewDto.setMachineId(machine.getMachine().getMachineId());
                        overviewDto.setMachineName(machine.getMachine().getMachineName());

                        overviewDto.setNumberOfProcesses(doneProcesssCount);
                        overviewDto.setRunTime((totalRunTime));
                        overviewDto.setStopTime(totalStopTime);
                        overviewDto.setPgTime(totalPgTime);
                        overviewDto.setEmptyTime(totalEmptyTime);
                        overviewDto.setErrorTime(totalErrorTime);
                        overviewDto.setPgTimeExpect(pgTimeExpected);
                        overviewDto.setGroupTarget(workingHourGoal);
                        overviewList.add(overviewDto);
                }
                return overviewList;
        }

        // checked
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

                String startDate = requestDto.getStartDate();
                String endDate = requestDto.getEndDate();
                requestDto.setStartDate(startDate);
                requestDto.setEndDate(endDate);
                TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);

                List<MachineKpi> machineKpiList = machineKpiRepository.findByGroup_groupIdAndMonthAndYear(
                                requestDto.getGroupId(), timePeriodInfo.getMonth(), timePeriodInfo.getYear());
                if (machineKpiList.isEmpty()) {
                        return new TotalRunTimeResponse(totalRunTimeMainProduct, runTimeOfRerun, runTimeOfLK,
                                        runTimeOfElectric,
                                        totalRunTimeOfPreparation, totalPgTime, totalOffsetTime, totalStopTime,
                                        totalErrorTime);
                }
                List<Integer> machineIds = machineKpiList.stream()
                                .map(kpi -> kpi.getMachine().getMachineId())
                                .distinct()
                                .toList();
                MachineDailySummaryByMachine sum = machineDailyRepository.sumByMachines(machineIds,
                                timePeriodInfo.getStart(), timePeriodInfo.getEnd(), requestDto.getShiftCode());

                totalRunTimeMainProduct = sum.mainProductSeconds() / 3600f;
                runTimeOfRerun = sum.reRunSeconds() / 3600f;
                runTimeOfLK = sum.lkSeconds() / 3600f;
                runTimeOfElectric = sum.electricSeconds() / 3600f;
                totalRunTimeOfPreparation = sum.preparationSeconds() / 3600f;
                totalErrorTime = sum.errorSeconds() / 3600f;
                totalPgTime = sum.runPgSeconds() / 3600f;
                totalOffsetTime = sum.runOffsetSeconds() / 3600f;
                totalStopTime = sum.stopSeconds() / 3600f;
                return new TotalRunTimeResponse(totalRunTimeMainProduct, runTimeOfRerun, runTimeOfLK, runTimeOfElectric,
                                totalRunTimeOfPreparation, totalPgTime, totalOffsetTime, totalStopTime, totalErrorTime);
        }

        // chưa check
        @Override
        public List<MachineRunTimeDto> getTop5GroupOverview(GroupEfficiencyRequestDto requestDto) {
                TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);

                List<MachineKpi> machineKpiList = machineKpiRepository.findByGroup_groupIdAndMonthAndYear(
                                requestDto.getGroupId(), timePeriodInfo.getMonth(), timePeriodInfo.getYear());
                if (machineKpiList == null || machineKpiList.isEmpty()) {
                        return List.of();
                }

                // tính lại
                // List<MachineRunTimeDto> top5MachineRunTime =
                // logRepository.findTop5MachineRunTimeByGroupAndTime(
                // requestDto.getGroupId(), timePeriodInfo.getMonth(), timePeriodInfo.getYear(),
                // timePeriodInfo.getStartDate(), timePeriodInfo.getEndDate());

                List<MachineRunTimeDto> top5MachineRunTime = machineDailyRepository.findTop5MachineRunTimeFromDaily(
                                requestDto.getGroupId(), timePeriodInfo.getMonth(), timePeriodInfo.getYear(),
                                timePeriodInfo.getStart(),
                                timePeriodInfo.getEnd(), requestDto.getShiftCode());
                return top5MachineRunTime;
        }

        // chưa check
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
                                .orElseThrow(() -> new BusinessException(
                                                "Group not found when export machine group statistic"));
                TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Thống kê nhóm máy");
                int rowIdx = 5;
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
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
                                TimePeriodInfo weekInfo = TimeRange.buildWeekTimePeriodInfo(timePeriodInfo, week);
                                if (weekInfo == null)
                                        continue;
                                GroupEfficiencyRequestDto weekDto = new GroupEfficiencyRequestDto();
                                weekDto.setGroupId(requestDto.getGroupId());
                                weekDto.setStartDate(Instant.ofEpochMilli(weekInfo.getEndDate())
                                                .atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter));
                                weekDto.setEndDate(Instant.ofEpochMilli(weekInfo.getEndDate())
                                                .atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter));
                                GroupEfficiencyRequestDto weekDtotemp = new GroupEfficiencyRequestDto();
                                weekDtotemp.setGroupId(requestDto.getGroupId());
                                weekDtotemp.setStartDate(Instant.ofEpochMilli(weekInfo.getStartDate())
                                                .atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter));
                                weekDtotemp.setEndDate(Instant.ofEpochMilli(weekInfo.getEndDate())
                                                .atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter));
                                TotalRunTimeResponse stats = getTotalRunTime(weekDto);
                                GroupEfficiencyResponseDto eff = groupEfficiencyService.getGroupEfficiency(weekDtotemp);
                                Row row = sheet.createRow(rowIdx++);
                                row.createCell(0).setCellValue("Week " + week);
                                row.createCell(1)
                                                .setCellValue(stats.getRunTimeOfMainProduct()
                                                                + stats.getRunTimeOfRerun()
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
                                                        .atZone(ZoneId.systemDefault()).toLocalDate()
                                                        .format(exportDateFormatter)
                                        + " - "
                                        + Instant.ofEpochMilli(timePeriodInfo.getEndDate())
                                                        .atZone(ZoneId.systemDefault()).toLocalDate()
                                                        .format(exportDateFormatter)
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
                                row.createCell(1)
                                                .setCellValue(stats.getRunTimeOfMainProduct()
                                                                + stats.getRunTimeOfRerun()
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

        @Override
        public List<MachineRunTimeDto> getTop5LowestOverview(GroupEfficiencyRequestDto requestDto) {
                TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);

                List<MachineKpi> machineKpiList = machineKpiRepository.findByGroup_groupIdAndMonthAndYear(
                                requestDto.getGroupId(), timePeriodInfo.getMonth(), timePeriodInfo.getYear());
                if (machineKpiList == null || machineKpiList.isEmpty()) {
                        return List.of();
                }
                // tính lại
                // List<MachineRunTimeDto> top5MachineRunTime =
                // logRepository.findTop5MachineLowestRunTimeByGroupAndTime(
                // requestDto.getGroupId(), timePeriodInfo.getMonth(), timePeriodInfo.getYear(),
                // timePeriodInfo.getStartDate(), timePeriodInfo.getEndDate());

                List<MachineRunTimeDto> top5MachineRunTime = machineDailyRepository.findTop5MachineRunTimeFromDaily(
                                requestDto.getGroupId(), timePeriodInfo.getMonth(), timePeriodInfo.getYear(),
                                timePeriodInfo.getStart(),
                                timePeriodInfo.getEnd(), requestDto.getShiftCode());
                return top5MachineRunTime;
        }
}
