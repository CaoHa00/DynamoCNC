package com.example.Dynamo_Backend.service.implementation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.TimePeriodInfo;
import com.example.Dynamo_Backend.dto.RequestDto.StatisticRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.HistoryProcessDto;
import com.example.Dynamo_Backend.dto.ResponseDto.MachineDetailStatisticDto;
import com.example.Dynamo_Backend.dto.ResponseDto.MachineEfficiencyResponseDto;
import com.example.Dynamo_Backend.entities.CurrentStatus;
import com.example.Dynamo_Backend.entities.DrawingCodeProcess;
import com.example.Dynamo_Backend.entities.GroupKpi;
import com.example.Dynamo_Backend.entities.Log;
import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.entities.MachineKpi;
import com.example.Dynamo_Backend.entities.ProcessTime;
import com.example.Dynamo_Backend.entities.Staff;
import com.example.Dynamo_Backend.exception.BusinessException;
import com.example.Dynamo_Backend.mapper.MachineKpiMapper;
import com.example.Dynamo_Backend.repository.GroupKpiRepository;
import com.example.Dynamo_Backend.repository.LogRepository;
import com.example.Dynamo_Backend.repository.MachineKpiRepository;

import com.example.Dynamo_Backend.repository.CurrentStatusRepository;
import com.example.Dynamo_Backend.repository.GroupKpiRepository;
import com.example.Dynamo_Backend.repository.MachineRepository;
import com.example.Dynamo_Backend.service.MachineDetailStatisticService;
import com.example.Dynamo_Backend.service.ProcessTimeService;
import com.example.Dynamo_Backend.util.DateTimeUtil;
import com.example.Dynamo_Backend.util.TimeRange;

@Service
public class MachineDetailStatisticImplementation implements MachineDetailStatisticService {
        @Autowired
        private MachineRepository machineRepository;

        @Autowired
        private MachineKpiRepository machineKpiRepository;

        @Autowired
        private LogRepository logRepository;

        private CurrentStatusRepository currentStatusRepository;

        @Autowired
        ProcessTimeService processTimeService;

        @Autowired
        GroupKpiRepository groupKpiRepository;

        public MachineDetailStatisticDto calculateMachineTime(Integer machineId, TimePeriodInfo timePeriodInfo) {

                Machine machine = machineRepository.findById(machineId)
                                .orElseThrow(() -> new BusinessException(
                                                "Machine not found when get detail statistic with ID: " + machineId));
                // get logs in time range
                List<Log> logs = machine.getLogs().stream()
                                .filter(log -> log.getTimeStamp() >= timePeriodInfo.getStartDate()
                                                && log.getTimeStamp() <= timePeriodInfo.getEndDate())
                                .toList();

                // logs.sort((log1, log2) -> Long.compare(log1.getTimeStamp(),
                // log2.getTimeStamp()));

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
                        if (log.getStatus().contains("E")) {
                                totalErrorTime += isLast
                                                ? (Math.min(timePeriodInfo.getEndDate(), System.currentTimeMillis())
                                                                - log.getTimeStamp())
                                                : (next.getTimeStamp() - log.getTimeStamp());
                        } else {
                                switch (status) {
                                        case "R1":
                                                totalPgTime += isLast
                                                                ? (Math.min(timePeriodInfo.getEndDate(),
                                                                                System.currentTimeMillis())
                                                                                - log.getTimeStamp())
                                                                : (next.getTimeStamp() - log.getTimeStamp());
                                                break;
                                        case "R2":
                                                totalOffsetTime += isLast
                                                                ? (Math.min(timePeriodInfo.getEndDate(),
                                                                                System.currentTimeMillis())
                                                                                - log.getTimeStamp())
                                                                : (next.getTimeStamp() - log.getTimeStamp());
                                                break;
                                        default:
                                                totalStopTime += isLast
                                                                ? (Math.min(timePeriodInfo.getEndDate(),
                                                                                System.currentTimeMillis())
                                                                                - log.getTimeStamp())
                                                                : (next.getTimeStamp() - log.getTimeStamp());
                                                break;
                                }
                        }
                }
                totalRunTime = totalPgTime + totalOffsetTime;
                return new MachineDetailStatisticDto(machineId, machine.getMachineName(), totalRunTime, 0f,
                                totalStopTime, 0f,
                                totalPgTime, 0f,
                                totalErrorTime, 0f,
                                numberOfProcesses, 0f, totalOffsetTime);
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
                MachineKpi machineKpi;
                List<MachineKpi> machines;

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
                        machines = machineKpiRepository.findByGroup_groupIdAndMonthAndYear(
                                        machineKpi.getGroup().getGroupId(), timePeriodInfo.getMonth(),
                                        timePeriodInfo.getYear());
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

                return new MachineEfficiencyResponseDto(machine.getMachineId(), machine.getMachineName(),
                                operationalEfficiency, pgEfficiency, valueEfficiency, oee, offsetLoss, otherLoss,
                                machines.stream().map(MachineKpiMapper::mapToMachineDto).toList());
        }

}
