package com.example.Dynamo_Backend.service.implementation;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.MachineDailySummaryByMachine;
import com.example.Dynamo_Backend.dto.MachineRunTimeDto;
import com.example.Dynamo_Backend.dto.TimePeriodInfo;
import com.example.Dynamo_Backend.dto.RequestDto.GroupEfficiencyRequestDto;
import com.example.Dynamo_Backend.dto.RequestDto.StatisticRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.*;
import com.example.Dynamo_Backend.entities.DrawingCodeProcess;
import com.example.Dynamo_Backend.entities.Group;
import com.example.Dynamo_Backend.entities.GroupKpi;
import com.example.Dynamo_Backend.entities.Log;
import com.example.Dynamo_Backend.entities.MachineDaily;
import com.example.Dynamo_Backend.entities.MachineKpi;
import com.example.Dynamo_Backend.entities.ProcessTime;
import com.example.Dynamo_Backend.exception.BusinessException;
import com.example.Dynamo_Backend.mapper.MachineKpiMapper;
import com.example.Dynamo_Backend.repository.*;
import com.example.Dynamo_Backend.service.MachineDetailStatisticService;
import com.example.Dynamo_Backend.service.MachineGroupStatisticService;
import com.example.Dynamo_Backend.service.MachineKpiService;
import com.example.Dynamo_Backend.service.ProcessTimeService;
import com.example.Dynamo_Backend.service.ReportService;
import com.example.Dynamo_Backend.util.DateTimeUtil;
import com.example.Dynamo_Backend.util.TimeRange;
import com.example.Dynamo_Backend.util.TypeUtil;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class MachineGroupStatisticImplementation implements MachineGroupStatisticService {

        @Autowired
        private com.example.Dynamo_Backend.service.GroupEfficiencyService groupEfficiencyService;
        @Autowired
        private MachineKpiRepository machineKpiRepository;
        @Autowired
        private MachineKpiService machineKpiService;

        @Autowired
        private GroupRepository groupRepository;;

        @Autowired
        private MachineDetailStatisticService machineStatsService;

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
                Float totalEmptyTime = 0f;
                Float totalErrorTime = 0f;
                Integer totalProcesses = 0;

                List<Integer> machineIds = machineKpiList.stream()
                                .map(kpi -> kpi.getMachine().getMachineId())
                                .toList();

                MachineDailySummaryByMachine sum = machineDailyRepository.sumByMachines(machineIds,
                                timePeriodInfo.getStart(), timePeriodInfo.getEnd(), shiftCode);
                Long value = sum.quantity();
                if (value != null && value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
                        totalProcesses = value.intValue();
                }
                if (sum.runPgSeconds() > 0)
                        totalPgTime = sum.runPgSeconds() / 3600f;

                totalOffsetTime = sum.runOffsetSeconds() / 3600f;
                totalRunTime = totalPgTime + totalOffsetTime;
                totalStopTime = sum.stopSeconds() / 3600f;
                totalErrorTime = sum.errorSeconds() / 3600f;
                totalEmptyTime = sum.emptySeconds() / 3600f;

                return new MachineGroupStatisticDto(groupId, "", totalRunTime, totalStopTime, totalPgTime,
                                totalOffsetTime, totalEmptyTime, totalErrorTime, totalErrorTime, 0f, 0f,
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
                Float totalEmptyTimeRate = 0f;
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

                if (previousPeriodStats.getTotalEmptyTime() != 0f) {
                        totalEmptyTimeRate = ((currentPeriodStats.getTotalEmptyTime()
                                        - previousPeriodStats.getTotalEmptyTime())
                                        / previousPeriodStats.getTotalEmptyTime()) * 100;
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
                currentPeriodStats.setTotalEmptyTime(currentPeriodStats.getTotalEmptyTime());
                return new MachineGroupStatisticDto(group.getGroupId(), group.getGroupName(),
                                currentPeriodStats.getTotalRunTime(), currentPeriodStats.getTotalStopTime(),
                                currentPeriodStats.getTotalPgTime(), currentPeriodStats.getTotalOffsetTime(),
                                currentPeriodStats.getTotalEmptyTime(), currentPeriodStats.getTotalErrorTime(),
                                (float) Math.round(totalErrorTimeRate * 100) / 100,
                                (float) Math.round(totalRunTimeRate * 100) / 100,
                                (float) Math.round(totalStopTimeRate * 100) / 100,
                                (float) Math.round(totalPgTimeRate * 100) / 100,
                                (float) Math.round(totalOffsetTimeRate * 100) / 100,
                                (float) Math.round(totalEmptyTimeRate * 100) / 100,
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

                String fileName = "ThongKeNhomMay.xlsx";

                Group group = groupRepository.findById(requestDto.getGroupId())
                                .orElseThrow(() -> new BusinessException(
                                                "Group not found when export machine group statistic"));

                TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);

                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Thống kê nhóm máy");

                /* ========== STYLES ========== */
                // Title style
                Font titleFont = workbook.createFont();
                titleFont.setBold(true);
                titleFont.setFontHeightInPoints((short) 14);

                CellStyle titleStyle = workbook.createCellStyle();
                titleStyle.setFont(titleFont);
                titleStyle.setAlignment(HorizontalAlignment.CENTER);

                // Header style
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);

                CellStyle headerStyle = workbook.createCellStyle();
                headerStyle.setFont(headerFont);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                headerStyle.setBorderTop(BorderStyle.THIN);
                headerStyle.setBorderBottom(BorderStyle.THIN);
                headerStyle.setBorderLeft(BorderStyle.THIN);
                headerStyle.setBorderRight(BorderStyle.THIN);
                headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                // Data style
                CellStyle dataStyle = workbook.createCellStyle();
                dataStyle.setAlignment(HorizontalAlignment.CENTER);
                dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                dataStyle.setBorderTop(BorderStyle.THIN);
                dataStyle.setBorderBottom(BorderStyle.THIN);
                dataStyle.setBorderLeft(BorderStyle.THIN);
                dataStyle.setBorderRight(BorderStyle.THIN);

                // Number style
                CellStyle numberStyle = workbook.createCellStyle();
                numberStyle.cloneStyleFrom(dataStyle);
                numberStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));

                int rowIdx = 0;

                /* ========== TITLE ========== */
                Row titleRow = sheet.createRow(rowIdx++);
                Cell titleCell = titleRow.createCell(0);
                titleCell.setCellValue("BÁO CÁO ĐỊNH KỲ NHÓM MÁY" + group.getGroupName() + "TỪ"
                                + timePeriodInfo.getStart().toString() + "TỚI" + timePeriodInfo.getEnd().toString());
                titleCell.setCellStyle(titleStyle);
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 14));

                rowIdx++; // empty row

                /* ========== HEADER ========== */
                String[] headers = {
                                "   Ngày   ", "   Máy   ", "   Ca   ", "   Giờ chạy(Giờ)   ",
                                "   Giờ chạy SP chính(Giờ)   ",
                                "   Giờ chạy NG_Chạy lại(Giờ)   ", "   Giờ chạy LK đồ gá(Giờ)   ",
                                "   Giờ chạy điện cực(Giờ)   ", "   Giờ chạy dự bị(Giờ)   ",
                                "   Giờ chạy PG(Giờ)   ", "   Giờ chạy Offset(Giờ)   ",
                                "   Giờ chạy Dừng(Giờ)   ", "   Giờ chạy Lỗi(Giờ)   ",
                                "   Giờ trống(Giờ)   ", "   Chi tiết hoàn thành   "
                };

                Row headerRow = sheet.createRow(rowIdx++);
                sheet.setAutoFilter(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), 0, 14));
                sheet.createFreezePane(2, headerRow.getRowNum() + 1);
                for (int i = 0; i < headers.length; i++) {
                        Cell cell = headerRow.createCell(i);
                        cell.setCellValue(headers[i]);
                        cell.setCellStyle(headerStyle);
                }

                /* ========== DATA ========== */
                List<Integer> machineIds = machineKpiService.determineMachineByMonthOrWeek(
                                group.getGroupId(), timePeriodInfo);

                List<MachineDaily> daily = machineDailyRepository.findByShiftAndMachinesAndDateRange(
                                requestDto.getShiftCode(),
                                machineIds,
                                timePeriodInfo.getStart(),
                                timePeriodInfo.getEnd());

                for (MachineDaily m : daily) {
                        Row row = sheet.createRow(rowIdx++);

                        createCell(row, 0, m.getLogDate().toString(), dataStyle);
                        createCell(row, 1, m.getMachineId(), dataStyle);
                        createCell(row, 2, m.getShiftCode(), dataStyle);

                        createCell(row, 3, m.getRunPgSeconds() / 3600f + m.getRunOffsetSeconds() / 3600f, numberStyle);
                        createCell(row, 4, m.getMainProductSeconds() / 3600f, numberStyle);
                        createCell(row, 5, m.getReRunSeconds() / 3600f, numberStyle);
                        createCell(row, 6, m.getLkSeconds() / 3600f, numberStyle);
                        createCell(row, 7, m.getElectricSeconds() / 3600f, numberStyle);
                        createCell(row, 8, m.getPreparationSeconds() / 3600f, numberStyle);
                        createCell(row, 9, m.getRunPgSeconds() / 3600f, numberStyle);
                        createCell(row, 10, m.getRunOffsetSeconds() / 3600f, numberStyle);
                        createCell(row, 11, m.getStopSeconds() / 3600f, numberStyle);
                        createCell(row, 12, m.getErrorSeconds() / 3600f, numberStyle);
                        createCell(row, 13, m.getEmptySeconds() / 3600f, numberStyle);
                        createCell(row, 14, m.getQuantity(), dataStyle);
                }

                /* ========== AUTO SIZE ========== */
                for (int i = 0; i <= 14; i++) {
                        sheet.autoSizeColumn(i);
                }

                /* ========== WRITE RESPONSE ========== */
                try {
                        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
                        workbook.write(response.getOutputStream());
                        response.flushBuffer();
                } catch (Exception e) {
                        throw new RuntimeException("Export Excel failed", e);
                } finally {
                        try {
                                workbook.close();
                        } catch (Exception ignore) {
                        }
                }
        }

        /* ===== Helper method ===== */
        private void createCell(Row row, int col, Object value, CellStyle style) {
                Cell cell = row.createCell(col);
                if (value instanceof Number) {
                        cell.setCellValue(((Number) value).doubleValue());
                } else {
                        cell.setCellValue(value != null ? value.toString() : "");
                }
                cell.setCellStyle(style);
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

                List<MachineRunTimeDto> top5MachineRunTime = machineDailyRepository.findTop5MachineRunTimeFromDailyLow(
                                requestDto.getGroupId(), timePeriodInfo.getMonth(), timePeriodInfo.getYear(),
                                timePeriodInfo.getStart(),
                                timePeriodInfo.getEnd(), requestDto.getShiftCode());
                return top5MachineRunTime;
        }
}
