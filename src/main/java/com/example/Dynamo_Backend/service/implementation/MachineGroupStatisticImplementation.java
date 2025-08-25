package com.example.Dynamo_Backend.service.implementation;

import java.util.ArrayList;
import java.util.List;

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
import com.example.Dynamo_Backend.repository.DrawingCodeProcessRepository;
import com.example.Dynamo_Backend.repository.GroupRepository;
import com.example.Dynamo_Backend.repository.MachineKpiRepository;
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

    @Override
    public MachineGroupStatisticDto getGroupStatistic(GroupEfficiencyRequestDto requestDto) {
        String startDate = requestDto.getStartDate().concat(" 00:00:00");
        String endDate = requestDto.getEndDate().concat(" 23:59:59");
        requestDto.setStartDate(startDate);
        requestDto.setEndDate(endDate);

        TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);
        Group group = groupRepository.findById(requestDto.getGroupId())
                .orElseThrow(() -> new BusinessException("Group not found when get machine group statistic"));
        List<MachineKpi> machineKpiList = machineKpiRepository.findByGroup_groupIdAndMonthAndYear(
                requestDto.getGroupId(), timePeriodInfo.getMonth(), timePeriodInfo.getYear());
        if (machineKpiList.isEmpty()) {
            return new MachineGroupStatisticDto(group.getGroupId(), group.getGroupName(), 0f, 0f, 0f, 0f, 0f, 0f, 0f,
                    0f, 0f,
                    0f, 0f, 0f);
        }

        Float totalRunTime = 0f;
        Float totalStopTime = 0f;
        Float totalPgTime = 0f;
        Float totalOffsetTime = 0f;
        Float totalSpanTime = 0f;
        Float totalRunTimeRate = 0f;
        Float totalStopTimeRate = 0f;
        Float totalPgTimeRate = 0f;
        Float totalOffsetTimeRate = 0f;
        Float totalSpanTimeRate = 0f;

        Float totalErrorTime = 0f;
        Float totalErrorTimeRate = 0f;

        for (MachineKpi machineKpi : machineKpiList) {
            List<DrawingCodeProcess> drawingCodeProcesses = processRepository
                    .findByMachine_MachineId(machineKpi.getMachine().getMachineId());
            if (drawingCodeProcesses != null && !drawingCodeProcesses.isEmpty()) {
                // calculate error time
                for (DrawingCodeProcess process : drawingCodeProcesses) {
                    if (process.getStartTime() > timePeriodInfo.getEndDate() ||
                            process.getEndTime() < timePeriodInfo.getStartDate()) {
                        continue;
                    }
                    ProcessTime processTime = process.getProcessTime();
                    totalOffsetTime += processTime.getOffsetTime();
                    totalPgTime += processTime.getPgTime();
                    totalRunTime += processTime.getRunTime();
                    totalSpanTime += processTime.getSpanTime();
                    totalStopTime += processTime.getStopTime();

                    List<Log> logs = process.getLogs();
                    if (logs == null || logs.isEmpty()) {
                        continue;
                    }
                    logs.sort((l1, l2) -> Long.compare(l1.getTimeStamp(), l2.getTimeStamp()));

                    // Sum error time
                    for (int i = 0; i < logs.size() - 1; i++) {
                        Log current = logs.get(i);
                        Log next = logs.get(i + 1);
                        if (current.getStatus().contains("E")) {
                            totalErrorTime += (next.getTimeStamp() - current.getTimeStamp());
                        }
                    }
                }
            }
        }

        Float previousTotalRunTime = 0f;
        Float previousTotalStopTime = 0f;
        Float previousTotalPgTime = 0f;
        Float previousTotalOffsetTime = 0f;
        Float previousTotalSpanTime = 0f;
        Float previousTotalErrorTime = 0f;
        TimePeriodInfo previousTime = TimeRange.getPreviousTimeRange(timePeriodInfo);
        List<MachineKpi> previousMachineKpiList = machineKpiRepository.findByGroup_groupIdAndMonthAndYear(
                requestDto.getGroupId(), previousTime.getMonth(), previousTime.getYear());
        if (previousMachineKpiList.isEmpty()) {
            return new MachineGroupStatisticDto(group.getGroupId(), group.getGroupName(), totalRunTime, totalStopTime,
                    totalPgTime, totalOffsetTime, totalSpanTime, totalErrorTime, 0f, 0f, 0f, 0f, 0f, 0f);
        }
        for (MachineKpi machineKpi : previousMachineKpiList) {
            List<DrawingCodeProcess> drawingCodeProcesses = processRepository
                    .findByMachine_MachineId(machineKpi.getMachine().getMachineId());
            if (drawingCodeProcesses != null && !drawingCodeProcesses.isEmpty()) {
                for (DrawingCodeProcess process : drawingCodeProcesses) {
                    if (process.getStartTime() > previousTime.getEndDate() ||
                            process.getEndTime() < previousTime.getStartDate()) {
                        continue;
                    }
                    ProcessTime processTime = process.getProcessTime();
                    if (processTime == null) {
                        processTime = processTimeService.calculateProcessTime(process);
                    }
                    previousTotalOffsetTime += processTime.getOffsetTime();
                    previousTotalPgTime += processTime.getPgTime();
                    previousTotalRunTime += processTime.getRunTime();
                    previousTotalSpanTime += processTime.getSpanTime();
                    previousTotalStopTime += processTime.getStopTime();
                    List<Log> logs = process.getLogs();
                    if (logs != null && !logs.isEmpty()) {
                        for (int i = 0; i < logs.size() - 1; i++) {
                            Log current = logs.get(i);
                            Log next = logs.get(i + 1);
                            if (current.getStatus().contains("E")) {
                                previousTotalErrorTime += (next.getTimeStamp() - current.getTimeStamp());
                            }
                        }
                    }
                }
            }
        }
        if (previousTotalOffsetTime != 0f) {
            totalOffsetTimeRate = ((totalOffsetTime - previousTotalOffsetTime) / previousTotalOffsetTime) * 100;
        }

        if (previousTotalPgTime != 0f) {
            totalPgTimeRate = ((totalPgTime - previousTotalPgTime) / previousTotalPgTime) * 100;
        }

        if (previousTotalRunTime != 0f) {
            totalRunTimeRate = ((totalRunTime - previousTotalRunTime) / previousTotalRunTime) * 100;
        }

        if (previousTotalSpanTime != 0f) {
            totalSpanTimeRate = ((totalSpanTime - previousTotalSpanTime) / previousTotalSpanTime) * 100;
        }

        if (previousTotalStopTime != 0f) {
            totalStopTimeRate = ((totalStopTime - previousTotalStopTime) / previousTotalStopTime) * 100;
        }

        if (previousTotalErrorTime != 0f) {
            totalErrorTimeRate = ((totalErrorTime - previousTotalErrorTime) / previousTotalErrorTime) * 100;
        }

        return new MachineGroupStatisticDto(group.getGroupId(), group.getGroupName(),
                totalRunTime, totalStopTime, totalPgTime, totalOffsetTime, totalSpanTime, totalErrorTime,
                totalErrorTimeRate,
                totalRunTimeRate, totalStopTimeRate, totalPgTimeRate, totalOffsetTimeRate, totalSpanTimeRate);
    }

    @Override
    public List<MachineGroupOverviewDto> getGroupOverview(GroupEfficiencyRequestDto requestDto) {
        String startDate = requestDto.getStartDate().concat(" 00:00:00");
        String endDate = requestDto.getEndDate().concat(" 23:59:59");
        requestDto.setStartDate(startDate);
        requestDto.setEndDate(endDate);

        TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(requestDto);
        List<MachineGroupOverviewDto> overviewList = new ArrayList<>();
        List<MachineKpi> machineKpiList = machineKpiRepository.findByGroup_groupIdAndMonthAndYear(
                requestDto.getGroupId(), timePeriodInfo.getMonth(), timePeriodInfo.getYear());
        for (MachineKpi machineKpi : machineKpiList) {
            MachineGroupOverviewDto overviewDto = new MachineGroupOverviewDto();
            overviewDto.setMachineId(machineKpi.getMachine().getMachineId());
            overviewDto.setMachineName(machineKpi.getMachine().getMachineName());
            Float totalRunTime = 0f;
            Float totalStopTime = 0f;
            Float totalPgTime = 0f;
            Float totalOffsetTime = 0f;
            Float totalSpanTime = 0f;
            Float totalPgTimeExpect = 0f;
            overviewDto.setNumberOfProcesses(0);
            List<DrawingCodeProcess> drawingCodeProcesses = processRepository
                    .findByMachine_MachineId(machineKpi.getMachine().getMachineId());
            if (drawingCodeProcesses != null && !drawingCodeProcesses.isEmpty()) {
                overviewDto.setNumberOfProcesses(drawingCodeProcesses.size());
                for (DrawingCodeProcess process : drawingCodeProcesses) {
                    totalPgTimeExpect += process.getPgTime();
                    if (process.getStartTime() > timePeriodInfo.getEndDate() ||
                            process.getEndTime() < timePeriodInfo.getStartDate()) {
                        continue;
                    }
                    ProcessTime processTime = process.getProcessTime();
                    if (processTime == null) {
                        processTime = processTimeService.calculateProcessTime(process);
                    }
                    totalOffsetTime += processTime.getOffsetTime();
                    totalPgTime += processTime.getPgTime();
                    totalRunTime += processTime.getRunTime();
                    totalSpanTime += processTime.getSpanTime();
                    totalStopTime += processTime.getStopTime();
                }
            }
            overviewDto.setRunTime(totalRunTime);
            overviewDto.setStopTime(totalStopTime);
            overviewDto.setPgTime(totalPgTime);
            overviewDto.setOffsetTime(totalOffsetTime);
            overviewDto.setSpanTime(totalSpanTime);
            overviewDto.setPgTimeExpect(totalPgTimeExpect);
            overviewList.add(overviewDto);
        }
        return overviewList;
    }

}
