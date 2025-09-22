package com.example.Dynamo_Backend.service.implementation;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.TimePeriodInfo;
import com.example.Dynamo_Backend.dto.RequestDto.GroupEfficiencyRequestDto;
import com.example.Dynamo_Backend.dto.RequestDto.StatisticRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.HistoryProcessDto;
import com.example.Dynamo_Backend.dto.ResponseDto.MachineDetailStatisticDto;
import com.example.Dynamo_Backend.dto.ResponseDto.MachineEfficiencyResponseDto;
import com.example.Dynamo_Backend.entities.CurrentStatus;
import com.example.Dynamo_Backend.entities.DrawingCodeProcess;
import com.example.Dynamo_Backend.entities.Group;
import com.example.Dynamo_Backend.entities.GroupKpi;
import com.example.Dynamo_Backend.entities.Log;
import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.entities.MachineKpi;
import com.example.Dynamo_Backend.entities.ProcessTime;
import com.example.Dynamo_Backend.entities.Staff;
import com.example.Dynamo_Backend.exception.BusinessException;
import com.example.Dynamo_Backend.mapper.MachineKpiMapper;
import com.example.Dynamo_Backend.repository.GroupKpiRepository;
import com.example.Dynamo_Backend.repository.GroupRepository;
import com.example.Dynamo_Backend.repository.MachineKpiRepository;

import com.example.Dynamo_Backend.repository.CurrentStatusRepository;
import com.example.Dynamo_Backend.repository.MachineRepository;
import com.example.Dynamo_Backend.service.MachineDetailStatisticService;
import com.example.Dynamo_Backend.service.ProcessTimeService;
import com.example.Dynamo_Backend.util.DateTimeUtil;
import com.example.Dynamo_Backend.util.TimeRange;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class MachineDetailStatisticImplementation implements MachineDetailStatisticService {
        @Autowired
        private MachineRepository machineRepository;

        @Autowired
        private MachineKpiRepository machineKpiRepository;

        @Autowired
        private CurrentStatusRepository currentStatusRepository;

        @Autowired
        ProcessTimeService processTimeService;

        @Autowired
        GroupKpiRepository groupKpiRepository;

        @Autowired
        private GroupRepository groupRepository;

        public MachineDetailStatisticDto calculateMachineTime(Integer machineId, TimePeriodInfo timePeriodInfo) {

                Machine machine = machineRepository.findById(machineId)
                                .orElseThrow(() -> new BusinessException(
                                                "Machine not found when get detail statistic with ID: " + machineId));

                List<Log> logs = machine.getLogs().stream()
                                .filter(log -> log.getTimeStamp() >= timePeriodInfo.getStartDate()
                                                && log.getTimeStamp() <= timePeriodInfo.getEndDate())
                                .sorted(Comparator.comparingLong(Log::getTimeStamp))
                                .toList();

                Float totalRunTime = 0f;
                Float totalStopTime = 0f;
                Float totalPgTime = 0f;
                Float totalErrorTime = 0f;
                Float totalOffsetTime = 0f;
                Integer numberOfProcesses = machine.getDrawingCodeProcesses().stream()
                                .filter(process -> process.getStartTime() <= timePeriodInfo.getEndDate()
                                                && process.getEndTime() >= timePeriodInfo.getStartDate())
                                .toList().size();
                boolean isLast = false;
                for (int i = 0; i < logs.size(); i++) {
                        Log log = logs.get(i);
                        String status = log.getStatus();

                        isLast = (i + 1 >= logs.size());
                        Log next = isLast ? null : logs.get(i + 1);
                        long duration = isLast ? 0 : next.getTimeStamp() - log.getTimeStamp();
                        if (duration < 0)
                                duration = 0;
                        if (log.getStatus().contains("E")) {
                                totalErrorTime += isLast
                                                ? (Math.min(timePeriodInfo.getEndDate(), System.currentTimeMillis())
                                                                - log.getTimeStamp())
                                                : duration;
                        } else {
                                switch (status) {
                                        case "R1":
                                                totalPgTime += isLast
                                                                ? (Math.min(timePeriodInfo.getEndDate(),
                                                                                System.currentTimeMillis())
                                                                                - log.getTimeStamp())
                                                                : duration;
                                                break;
                                        case "R2":
                                                totalOffsetTime += isLast
                                                                ? (Math.min(timePeriodInfo.getEndDate(),
                                                                                System.currentTimeMillis())
                                                                                - log.getTimeStamp())
                                                                : duration;
                                                break;
                                        default:
                                                totalStopTime += isLast
                                                                ? (Math.min(timePeriodInfo.getEndDate(),
                                                                                System.currentTimeMillis())
                                                                                - log.getTimeStamp())
                                                                : duration;
                                                break;
                                }
                        }
                }
                totalRunTime = totalPgTime + totalOffsetTime;
                return new MachineDetailStatisticDto(machineId, machine.getMachineName(), totalRunTime / 3600000f, 0f,
                                totalStopTime / 3600000f, 0f,
                                totalPgTime / 3600000f, 0f,
                                totalErrorTime / 3600000f, 0f,
                                numberOfProcesses, 0f, totalOffsetTime / 3600000f);
        }

        @Override
        public MachineDetailStatisticDto getMachineDetailStatistic(StatisticRequestDto requestDto) {
                String startDate = requestDto.getStartDate().concat(" 00:00:00");
                String endDate = requestDto.getEndDate().concat(" 23:59:59");
                requestDto.setStartDate(startDate);
                requestDto.setEndDate(endDate);

                TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);
                TimePeriodInfo previousTimePeriodInfo = TimeRange.getPreviousTimeRange(timePeriodInfo);

                MachineDetailStatisticDto current = calculateMachineTime(requestDto.getId(), timePeriodInfo);
                MachineDetailStatisticDto previous = calculateMachineTime(requestDto.getId(),
                                previousTimePeriodInfo);

                if (previous.getTotalRunTime() != 0) {
                        current.setRunTimeRate(
                                        (current.getTotalRunTime() - previous.getTotalRunTime())
                                                        / previous.getTotalRunTime() * 100);
                }
                if (previous.getTotalStopTime() != 0) {
                        current.setStopTimeRate(
                                        (current.getTotalStopTime() - previous.getTotalStopTime())
                                                        / previous.getTotalStopTime() * 100);
                }
                if (previous.getTotalPgTime() != 0) {
                        current.setPgTimeRate(
                                        (current.getTotalPgTime() - previous.getTotalPgTime())
                                                        / previous.getTotalPgTime() * 100);
                }
                if (previous.getTotalErrorTime() != 0) {
                        current.setErrorTimeRate(
                                        (current.getTotalErrorTime() - previous.getTotalErrorTime())
                                                        / previous.getTotalErrorTime() * 100);
                }
                if (previous.getNumberOfProcesses() != 0) {
                        current.setProcessRate((current.getNumberOfProcesses() - previous.getNumberOfProcesses())
                                        / (float) previous.getNumberOfProcesses() * 100);
                }
                return current;
        }

        @Override
        public List<HistoryProcessDto> getMachineHistoryProcess(StatisticRequestDto requestDto) {
                String startDate = requestDto.getStartDate().concat(" 00:00:00");
                String endDate = requestDto.getEndDate().concat(" 23:59:59");
                requestDto.setStartDate(startDate);
                requestDto.setEndDate(endDate);

                TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);
                Machine machine = machineRepository.findById(requestDto.getId())
                                .orElseThrow(() -> new BusinessException(
                                                "Machine not found when get detail history with ID: "
                                                                + requestDto.getId()));
                List<DrawingCodeProcess> processes = machine.getDrawingCodeProcesses().stream()
                                .filter(process -> process.getStartTime() <= timePeriodInfo.getEndDate()
                                                && process.getEndTime() >= timePeriodInfo.getStartDate())
                                .toList();

                List<HistoryProcessDto> historyProcessDtos = new ArrayList<>();
                for (DrawingCodeProcess process : processes) {
                        String status = "";
                        if (process.getProcessStatus() == 3) {
                                status = "Completed";
                        } else if (process.getProcessStatus() == 2) {
                                CurrentStatus currentStatus = currentStatusRepository
                                                .findByMachineId(process.getMachine().getMachineId());
                                if (currentStatus != null) {
                                        status = currentStatus.getStatus();
                                }
                        }
                        Staff staff = process.getOperateHistories().get(process.getOperateHistories().size() - 1)
                                        .getStaff();
                        String endTime = process.getEndTime() < process.getStartTime()
                                        ? DateTimeUtil.convertTimestampToString(
                                                        System.currentTimeMillis())
                                        : DateTimeUtil.convertTimestampToString(process.getEndTime());
                        HistoryProcessDto history = new HistoryProcessDto(
                                        process.getOrderDetail().getOrderCode(),
                                        process.getPartNumber(),
                                        process.getStepNumber(),
                                        DateTimeUtil.convertTimestampToString(process.getStartTime()),
                                        endTime,
                                        machine.getMachineName(),
                                        staff.getStaffId(),
                                        staff.getStaffName(),
                                        status);
                        historyProcessDtos.add(history);
                }
                return historyProcessDtos;
        }

        @Override
        public MachineEfficiencyResponseDto getMachineEfficiency(StatisticRequestDto requestDto) {
                String startDate = requestDto.getStartDate().concat(" 00:00:00"); // Should be "2025-07-21"
                String endDate = requestDto.getEndDate().concat(" 23:59:59");
                requestDto.setStartDate(startDate);
                requestDto.setEndDate(endDate);

                TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);
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

                Machine machine = null;
                MachineKpi machineKpi = null;
                List<MachineKpi> machines = null;

                if (requestDto.getId() == null) {
                        machines = machineKpiRepository.findByGroup_groupIdAndMonthAndYear(
                                        requestDto.getGroupId(), timePeriodInfo.getMonth(), timePeriodInfo.getYear());

                        machine = machineRepository.findById(machines.get(0).getMachine().getMachineId())
                                        .orElseThrow(() -> new BusinessException(
                                                        "Machine not found when get detail statistic with ID: "
                                                                        + requestDto.getId()));
                } else {
                        machine = machineRepository.findById(requestDto.getId())
                                        .orElseThrow(() -> new BusinessException(
                                                        "Machine not found when get detail statistic with ID: "
                                                                        + requestDto.getId()));
                        machineKpi = machineKpiRepository.findByMachine_machineIdAndMonthAndYear(
                                        machine.getMachineId(), timePeriodInfo.getMonth(), timePeriodInfo.getYear());
                        if (machineKpi != null) {
                                machines = machineKpiRepository.findByGroup_groupIdAndMonthAndYear(
                                                machineKpi.getGroup().getGroupId(), timePeriodInfo.getMonth(),
                                                timePeriodInfo.getYear());
                        }
                }

                // MachineKpi machineKpi =
                // machineKpiRepository.findByMachine_machineIdAndMonthAndYear(
                // machine.getMachineId(), timePeriodInfo.getMonth(), timePeriodInfo.getYear());
                // List<MachineKpi> machines =
                // machineKpiRepository.findByGroup_groupIdAndMonthAndYear(
                // machineKpi.getGroup().getGroupId(), timePeriodInfo.getMonth(),
                // timePeriodInfo.getYear());

                List<DrawingCodeProcess> processes = machine.getDrawingCodeProcesses();
                for (DrawingCodeProcess process : processes) {
                        if (process.getStartTime() <= timePeriodInfo.getEndDate() &&
                                        process.getEndTime() >= timePeriodInfo.getStartDate()) {
                                ProcessTime processTime = process.getProcessTime();
                                if (processTime == null)
                                        processTime = processTimeService.calculateProcessTime(process);
                                totalRunTime += processTime.getRunTime();
                                totalPgTime += processTime.getPgTime();
                                totalOffsetTime += processTime.getOffsetTime();
                                if (process.getProcessType().equals("SP_Chính")
                                                || process.getProcessType().equals("Điện cực")) {
                                        mainAndElectricProductPgTime += processTime.getPgTime();
                                } else {
                                        otherProductPgTime += processTime.getPgTime();
                                }
                        }
                }
                if (timePeriodInfo.isMonth()) {
                        groupKpi = groupKpiRepository.findByGroup_GroupIdAndIsMonthAndMonthAndYear(
                                        requestDto.getGroupId(), 1, timePeriodInfo.getMonth(), timePeriodInfo.getYear())
                                        .orElseGet(GroupKpi::new);
                } else {
                        groupKpi = groupKpiRepository.findByGroup_GroupIdAndWeekAndMonthAndYear(
                                        requestDto.getGroupId(), timePeriodInfo.getWeek(), timePeriodInfo.getMonth(),
                                        timePeriodInfo.getYear())
                                        .orElseGet(GroupKpi::new);
                }
                if (groupKpi.getWorkingHour() != null && groupKpi.getWorkingHour() > 0) {
                        operationalEfficiency = (totalRunTime / groupKpi.getWorkingHour()) * 100;
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
                if (machines == null)
                        machines = new ArrayList<>();

                return new MachineEfficiencyResponseDto(machine.getMachineId(), machine.getMachineName(),
                                operationalEfficiency, pgEfficiency, valueEfficiency, oee, offsetLoss, otherLoss,
                                machines.stream().map(MachineKpiMapper::mapToMachineDto).toList());
        }

        @Override
        public void exportExcelToResponse(StatisticRequestDto requestDto, HttpServletResponse response) {
                String fileName = "Data.xlsx";
                String title = "";
                String startDate = requestDto.getStartDate().concat(" 00:00:00");
                String endDate = requestDto.getEndDate().concat(" 23:59:59");
                requestDto.setStartDate(startDate);
                requestDto.setEndDate(endDate);
                Machine machine = machineRepository.findById(requestDto.getId())
                                .orElseThrow(() -> new BusinessException(
                                                "Machine not found when get detail statistic with ID: "
                                                                + requestDto.getId()));
                TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Thống kê máy");
                int rowIdx = 5;
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                DateTimeFormatter exportDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                Row headerRow = sheet.createRow(rowIdx++);
                headerRow.createCell(0).setCellValue("Thời gian");
                headerRow.createCell(1).setCellValue("Giờ chạy");
                headerRow.createCell(2).setCellValue("Giờ chạy PG ");
                headerRow.createCell(3).setCellValue("Giờ chạy Offset ");
                headerRow.createCell(4).setCellValue("Giờ Dừng ");
                headerRow.createCell(5).setCellValue("Giờ Lỗi ");
                headerRow.createCell(6).setCellValue("Hiệu suất vận hành");
                headerRow.createCell(7).setCellValue("Hiệu suất PG");
                headerRow.createCell(8).setCellValue("Hiệu suất Giá trị");
                headerRow.createCell(9).setCellValue("OEE");
                headerRow.createCell(10).setCellValue("Tổn thất Offset");
                headerRow.createCell(11).setCellValue("Tổn thất khác");

                if (timePeriodInfo.isMonth()) {
                        title = "Thống kê máy " + machine.getMachineId() + " tháng " +
                                        timePeriodInfo.getMonth() + "/"
                                        + timePeriodInfo.getYear() + ".xlsx";
                        for (int week = 1; week <= 4; week++) {
                                TimePeriodInfo weekInfo = TimeRange.buildWeekTimePeriodInfo(timePeriodInfo, week);
                                if (weekInfo == null)
                                        continue;
                                MachineDetailStatisticDto stats = calculateMachineTime(requestDto.getId(), weekInfo);
                                StatisticRequestDto weekDto = requestDto;
                                weekDto.setStartDate(Instant.ofEpochMilli(weekInfo.getStartDate())
                                                .atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter));
                                weekDto.setEndDate(Instant.ofEpochMilli(weekInfo.getEndDate())
                                                .atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter));
                                MachineEfficiencyResponseDto eff = getMachineEfficiency(weekDto);
                                Row row = sheet.createRow(rowIdx++);
                                row.createCell(0).setCellValue("Tuần " + week);
                                row.createCell(1)
                                                .setCellValue(stats.getTotalRunTime());
                                row.createCell(2).setCellValue(stats.getTotalPgTime());
                                row.createCell(3).setCellValue(stats.getTotalOffsetTime());
                                row.createCell(4).setCellValue(stats.getTotalStopTime());
                                row.createCell(5).setCellValue(stats.getTotalErrorTime());
                                row.createCell(6).setCellValue(eff.getOperationalEfficiency());
                                row.createCell(7).setCellValue(eff.getPgEfficiency());
                                row.createCell(8).setCellValue(eff.getValueEfficiency());
                                row.createCell(9).setCellValue(eff.getOee());
                                row.createCell(10).setCellValue(eff.getOffsetLoss());
                                row.createCell(11).setCellValue(eff.getOtherLoss());
                        }
                } else if (timePeriodInfo.getDay() <= 7) {
                        title = "Thống kê máy " + machine.getMachineId() + " "
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
                                StatisticRequestDto dayDto = new StatisticRequestDto();
                                dayDto.setId(requestDto.getId());
                                dayDto.setGroupId(requestDto.getGroupId());
                                dayDto.setStartDate(day.format(dateFormatter));
                                dayDto.setEndDate(day.format(dateFormatter));
                                MachineEfficiencyResponseDto eff = getMachineEfficiency(dayDto);

                                TimePeriodInfo dayInfo = TimeRange.getRangeTypeAndWeek(dayDto);
                                MachineDetailStatisticDto stats = calculateMachineTime(requestDto.getId(), dayInfo);
                                Row row = sheet.createRow(rowIdx++);
                                row.createCell(0).setCellValue(day.format(dateFormatter));
                                row.createCell(1).setCellValue(stats.getTotalRunTime());
                                row.createCell(2).setCellValue(stats.getTotalPgTime());
                                row.createCell(3).setCellValue(stats.getTotalOffsetTime());
                                row.createCell(4).setCellValue(stats.getTotalStopTime());
                                row.createCell(5).setCellValue(stats.getTotalErrorTime());
                                row.createCell(6).setCellValue(eff.getOperationalEfficiency());
                                row.createCell(7).setCellValue(eff.getPgEfficiency());
                                row.createCell(8).setCellValue(eff.getValueEfficiency());
                                row.createCell(9).setCellValue(eff.getOee());
                                row.createCell(10).setCellValue(eff.getOffsetLoss());
                                row.createCell(11).setCellValue(eff.getOtherLoss());
                        }
                }
                Row titleRow = sheet.createRow(1);
                titleRow.createCell(4).setCellValue(title.replace(".xlsx", ""));
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
        public void exportExcelGroupMachinesToResponse(GroupEfficiencyRequestDto requestDto,
                        HttpServletResponse response) {
                String fileName = "Data.xlsx";
                String title = "";
                String startDate = requestDto.getStartDate().concat(" 00:00:00");
                String endDate = requestDto.getEndDate().concat(" 23:59:59");
                requestDto.setStartDate(startDate);
                requestDto.setEndDate(endDate);
                TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);
                Group group = groupRepository.findById(requestDto.getGroupId())
                                .orElseThrow(() -> new BusinessException(
                                                "Group not found when get machine group statistic"));
                List<MachineKpi> machineKpiList = machineKpiRepository.findByGroup_groupIdAndMonthAndYear(
                                group.getGroupId(), timePeriodInfo.getMonth(), timePeriodInfo.getYear());

                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Thống kê máy");
                int rowIdx = 5;
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                DateTimeFormatter exportDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                Row headerRow = sheet.createRow(rowIdx++);
                headerRow.createCell(0).setCellValue("ID Máy");
                headerRow.createCell(1).setCellValue("Tên Máy");
                headerRow.createCell(2).setCellValue("Thời gian");
                headerRow.createCell(3).setCellValue("Giờ chạy");
                headerRow.createCell(4).setCellValue("Giờ chạy PG ");
                headerRow.createCell(5).setCellValue("Giờ chạy Offset ");
                headerRow.createCell(6).setCellValue("Giờ Dừng ");
                headerRow.createCell(7).setCellValue("Giờ Lỗi ");
                headerRow.createCell(8).setCellValue("Hiệu suất vận hành");
                headerRow.createCell(9).setCellValue("Hiệu suất PG");
                headerRow.createCell(10).setCellValue("Hiệu suất Giá trị");
                headerRow.createCell(11).setCellValue("OEE");
                headerRow.createCell(12).setCellValue("Tổn thất Offset");
                headerRow.createCell(13).setCellValue("Tổn thất khác");

                if (machineKpiList.isEmpty()) {
                        Row row = sheet.createRow(rowIdx++);
                        row.createCell(0).setCellValue("Không có dữ liệu");
                } else {
                        if (timePeriodInfo.isMonth()) {
                                title = "Thống kê nhóm " + group.getGroupName() + " tháng " +
                                                timePeriodInfo.getMonth() + "/"
                                                + timePeriodInfo.getYear() + ".xlsx";
                                for (MachineKpi mk : machineKpiList) {
                                        Machine machine = mk.getMachine();
                                        for (int week = 1; week <= 4; week++) {
                                                TimePeriodInfo weekInfo = TimeRange.buildWeekTimePeriodInfo(
                                                                timePeriodInfo,
                                                                week);
                                                if (weekInfo == null)
                                                        continue;
                                                MachineDetailStatisticDto stats = calculateMachineTime(
                                                                machine.getMachineId(),
                                                                weekInfo);
                                                StatisticRequestDto weekDto = new StatisticRequestDto(
                                                                group.getGroupId(),
                                                                machine.getMachineId(), null, null);
                                                weekDto.setStartDate(Instant.ofEpochMilli(weekInfo.getStartDate())
                                                                .atZone(ZoneId.systemDefault()).toLocalDate()
                                                                .format(dateFormatter));
                                                weekDto.setEndDate(Instant.ofEpochMilli(weekInfo.getEndDate())
                                                                .atZone(ZoneId.systemDefault()).toLocalDate()
                                                                .format(dateFormatter));
                                                MachineEfficiencyResponseDto eff = getMachineEfficiency(weekDto);
                                                Row row = sheet.createRow(rowIdx++);
                                                row.createCell(0).setCellValue(machine.getMachineId());
                                                row.createCell(1).setCellValue(machine.getMachineName());
                                                row.createCell(2).setCellValue("Tuần " + week);
                                                row.createCell(3).setCellValue(stats.getTotalRunTime());
                                                row.createCell(4).setCellValue(stats.getTotalPgTime());
                                                row.createCell(5).setCellValue(stats.getTotalOffsetTime());
                                                row.createCell(6).setCellValue(stats.getTotalStopTime());
                                                row.createCell(7).setCellValue(stats.getTotalErrorTime());
                                                row.createCell(8).setCellValue(eff.getOperationalEfficiency());
                                                row.createCell(9).setCellValue(eff.getPgEfficiency());
                                                row.createCell(10).setCellValue(eff.getValueEfficiency());
                                                row.createCell(11).setCellValue(eff.getOee());
                                                row.createCell(12).setCellValue(eff.getOffsetLoss());
                                                row.createCell(13).setCellValue(eff.getOtherLoss());
                                        }
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
                                for (MachineKpi mk : machineKpiList) {
                                        Machine machine = mk.getMachine();
                                        for (int i = 0; i < days; i++) {
                                                LocalDate day = start.plusDays(i);
                                                StatisticRequestDto dayDto = new StatisticRequestDto();
                                                dayDto.setId(machine.getMachineId());
                                                dayDto.setGroupId(requestDto.getGroupId());
                                                dayDto.setStartDate(day.format(dateFormatter));
                                                dayDto.setEndDate(day.format(dateFormatter));
                                                MachineEfficiencyResponseDto eff = getMachineEfficiency(dayDto);

                                                TimePeriodInfo dayInfo = TimeRange.getRangeTypeAndWeek(dayDto);
                                                MachineDetailStatisticDto stats = calculateMachineTime(
                                                                machine.getMachineId(),
                                                                dayInfo);
                                                Row row = sheet.createRow(rowIdx++);
                                                row.createCell(0).setCellValue(day.format(dateFormatter));
                                                row.createCell(1).setCellValue(stats.getTotalRunTime());
                                                row.createCell(2).setCellValue(stats.getTotalPgTime());
                                                row.createCell(3).setCellValue(stats.getTotalOffsetTime());
                                                row.createCell(4).setCellValue(stats.getTotalStopTime());
                                                row.createCell(5).setCellValue(stats.getTotalErrorTime());
                                                row.createCell(6).setCellValue(eff.getOperationalEfficiency());
                                                row.createCell(7).setCellValue(eff.getPgEfficiency());
                                                row.createCell(8).setCellValue(eff.getValueEfficiency());
                                                row.createCell(9).setCellValue(eff.getOee());
                                                row.createCell(10).setCellValue(eff.getOffsetLoss());
                                                row.createCell(11).setCellValue(eff.getOtherLoss());
                                        }
                                }
                        }
                }

                Row titleRow = sheet.createRow(1);
                titleRow.createCell(4).setCellValue(title.replace(".xlsx", ""));
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
