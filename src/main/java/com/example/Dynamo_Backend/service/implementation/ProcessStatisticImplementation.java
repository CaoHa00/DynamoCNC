package com.example.Dynamo_Backend.service.implementation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.RequestDto.GroupEfficiencyRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.DrawingCodeProcessStatistic;
import com.example.Dynamo_Backend.dto.ResponseDto.ProcessOverviewDto;
import com.example.Dynamo_Backend.entities.DrawingCodeProcess;
import com.example.Dynamo_Backend.entities.ProcessTime;
import com.example.Dynamo_Backend.repository.DrawingCodeProcessRepository;
import com.example.Dynamo_Backend.service.ProcessStatisticService;
import com.example.Dynamo_Backend.service.ProcessTimeService;
import com.example.Dynamo_Backend.util.DateTimeUtil;

@Service
public class ProcessStatisticImplementation implements ProcessStatisticService {

    @Autowired
    private DrawingCodeProcessRepository drawingCodeProcessRepository;

    @Autowired
    private ProcessTimeService processTimeService;

    @Override
    public DrawingCodeProcessStatistic getStatisticsForProcess(GroupEfficiencyRequestDto requestDto) {
        String startDate = requestDto.getStartDate().concat(" 00:00:00"); // Should be "2025-07-21"
        String endDate = requestDto.getEndDate().concat(" 23:59:59");

        Long startTimestamp = DateTimeUtil.convertStringToTimestamp(startDate);
        Long endTimestamp = DateTimeUtil.convertStringToTimestamp(endDate);

        Float totalRunTime = 0f;
        Float totalPgTime = 0f;
        Float pgTimeRate = 0f;
        Float pgTimeGoal = 0f;
        DrawingCodeProcessStatistic statistic = new DrawingCodeProcessStatistic();
        statistic.setNumberOfProcess(0);
        List<DrawingCodeProcess> processes = drawingCodeProcessRepository.findProcessesInRange(startTimestamp,
                endTimestamp);
        if (!processes.isEmpty()) {
            statistic.setNumberOfProcess(processes.size());
            for (DrawingCodeProcess process : processes) {
                ProcessTime processTime = process.getProcessTime();
                if (processTime == null) {
                    processTime = processTimeService.calculateProcessTime(process);
                }
                totalRunTime += processTime.getRunTime();
                totalPgTime += processTime.getPgTime();
                pgTimeGoal += process.getPgTime();
            }
            if (pgTimeGoal != 0f) {
                // positive value: pgTime is above pgTimeGoal and else
                pgTimeRate = ((totalPgTime - pgTimeGoal) / pgTimeGoal) * 100;
            }
        }

        statistic.setTotalRunTime(totalRunTime);
        statistic.setTotalPgTime(totalPgTime);
        statistic.setPgTimeDiffRate(pgTimeRate);

        return statistic;
    }

    @Override
    public List<ProcessOverviewDto> getProcessOverview(GroupEfficiencyRequestDto requestDto) {
        String startDate = requestDto.getStartDate().concat(" 00:00:00"); // Should be "2025-07-21"
        String endDate = requestDto.getEndDate().concat(" 23:59:59");

        Long startTimestamp = DateTimeUtil.convertStringToTimestamp(startDate);
        Long endTimestamp = DateTimeUtil.convertStringToTimestamp(endDate);
        List<DrawingCodeProcess> processes = drawingCodeProcessRepository.findProcessesInRange(startTimestamp,
                endTimestamp);
        List<ProcessOverviewDto> overviewList = new ArrayList<>();
        if (processes.isEmpty()) {
            return overviewList;
        }
        for (DrawingCodeProcess process : processes) {
            ProcessOverviewDto overviewDto = new ProcessOverviewDto();
            overviewDto.setOrderCode(process.getOrderDetail().getOrderCode());
            overviewDto.setPartNumber(process.getPartNumber());
            overviewDto.setStepNumber(process.getStepNumber());

            ProcessTime processTime = process.getProcessTime();
            if (processTime == null) {
                processTime = processTimeService.calculateProcessTime(process);
            }
            overviewDto.setPgTime(processTime.getPgTime());
            // positive value: pgTime is above pgTimeGoal and else
            if (process.getPgTime() != 0f) {
                overviewDto.setPgTimeDifference(
                        ((processTime.getPgTime() - process.getPgTime()) / process.getPgTime()) * 100);
            } else {
                overviewDto.setPgTimeDifference(0f);
            }
            overviewList.add(overviewDto);
        }
        return overviewList;
    }

}
