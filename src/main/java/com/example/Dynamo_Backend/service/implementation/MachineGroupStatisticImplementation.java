package com.example.Dynamo_Backend.service.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

@Service
public class MachineGroupStatisticImplementation implements MachineGroupStatisticService {
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

    // ??truong hop log cuoi cung nen tinh nhu nao: se co khoang trong giua log cuoi
    // cung va thoi gian ket thuc
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
            List<Log> logs = logsByMachine.get(machineId);
            List<DrawingCodeProcess> processes = processRepository.findByMachine_MachineId(machineId);

            // Group logs by process
            Map<String, List<Log>> logsByProcess = new java.util.HashMap<>();
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
                List<Log> logsInProcess = logs.stream()
                        .filter(log -> log.getTimeStamp() >= process.getStartTime()
                                && log.getTimeStamp() <= process.getEndTime())
                        .collect(Collectors.toList());
                logsByProcess.put(process.getProcessId(), logsInProcess);
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

}
