package com.example.Dynamo_Backend.service.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.TimePeriodInfo;
import com.example.Dynamo_Backend.dto.RequestDto.GroupEfficiencyRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.OrderCodeOverviewDto;
import com.example.Dynamo_Backend.dto.ResponseDto.OrderDetailStatisticDto;
import com.example.Dynamo_Backend.entities.DrawingCodeProcess;
import com.example.Dynamo_Backend.entities.Order;
import com.example.Dynamo_Backend.entities.OrderDetail;
import com.example.Dynamo_Backend.entities.ProcessTime;
import com.example.Dynamo_Backend.repository.DrawingCodeProcessRepository;
import com.example.Dynamo_Backend.repository.OrderDetailRepository;
import com.example.Dynamo_Backend.service.OrderDetailStatisticService;
import com.example.Dynamo_Backend.service.ProcessTimeService;
import com.example.Dynamo_Backend.util.TimeRange;

@Service
public class OrderDetailStatisticImplementation implements OrderDetailStatisticService {
        @Autowired
        private OrderDetailRepository orderDetailRepository;

        @Autowired
        private DrawingCodeProcessRepository drawingCodeProcessRepository;

        @Autowired
        private ProcessTimeService processTimeService;

        @Override
        public OrderDetailStatisticDto getOrderDetailStatistics(GroupEfficiencyRequestDto request) {
                String startDate = request.getStartDate().concat(" 00:00:00");
                String endDate = request.getEndDate().concat(" 23:59:59");
                request.setStartDate(startDate);
                request.setEndDate(endDate);
                TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(request);
                TimePeriodInfo previousTimePeriodInfo = TimeRange.getPreviousTimeRange(timePeriodInfo);

                List<DrawingCodeProcess> processes = drawingCodeProcessRepository
                                .findProcessesInRange(timePeriodInfo.getStartDate(),
                                                timePeriodInfo.getEndDate());
                List<DrawingCodeProcess> previousProcesses = drawingCodeProcessRepository
                                .findProcessesInRange(previousTimePeriodInfo.getStartDate(),
                                                previousTimePeriodInfo.getEndDate());

                Map<String, List<DrawingCodeProcess>> processByOrderDetail = processes.stream()
                                .collect(
                                                Collectors.groupingBy(process -> process.getOrderDetail()
                                                                .getOrderDetailId()));
                Map<String, List<DrawingCodeProcess>> previousProcessByOrderDetail = previousProcesses.stream()
                                .collect(
                                                Collectors.groupingBy(process -> process.getOrderDetail()
                                                                .getOrderDetailId()));
                Integer numberOfOrderDetails = processByOrderDetail.size();
                Integer previousNumberOfOrderDetails = previousProcessByOrderDetail.size();
                Integer numberOfProcess = processes.size();
                Integer previousNumberOfProcess = previousProcesses.size();
                Float orderDetailRate = previousNumberOfOrderDetails == 0 ? 0f
                                : (float) (numberOfOrderDetails - previousNumberOfOrderDetails)
                                                / previousNumberOfOrderDetails * 100;
                Float processRate = previousNumberOfProcess == 0 ? 0f
                                : (float) (numberOfProcess - previousNumberOfProcess) / previousNumberOfProcess * 100;
                Float totalTime = 0f;
                Float previousTotalTime = 0f;
                Float timeRate = 0f;
                // sum spanTime of all processes
                for (DrawingCodeProcess process : processes) {
                        if (process.getEndTime() != null && process.getStartTime() != null) {
                                totalTime += (process.getEndTime() - process.getStartTime()) / 3600000f;
                        }
                }
                for (DrawingCodeProcess process : previousProcesses) {
                        if (process.getEndTime() != null && process.getStartTime() != null) {
                                previousTotalTime += (process.getEndTime() - process.getStartTime()) / 3600000f;
                        }
                }
                // calculate time rate
                timeRate = previousTotalTime == 0 ? 0f
                                : (float) (totalTime - previousTotalTime) / previousTotalTime * 100;

                Map<String, Float> processType = processes.stream()
                                .collect(Collectors.groupingBy(DrawingCodeProcess::getProcessType,
                                                Collectors.summingDouble(process -> 1)))
                                .entrySet().stream()
                                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().floatValue()));

                Map<String, Float> groupPieChart = processes.stream()
                                .collect(Collectors.groupingBy(process -> {
                                        String groupName = process.getOrderDetail().getManagerGroup().getGroupName();
                                        return groupName != null ? groupName : "Chưa xác định";
                                }, Collectors.summingDouble(process -> 1)))
                                .entrySet().stream()
                                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().floatValue()));

                return new OrderDetailStatisticDto(numberOfOrderDetails, numberOfProcess, totalTime, orderDetailRate,
                                timeRate, processRate, processType, groupPieChart);
        }

        @Override
        public List<OrderCodeOverviewDto> getOrderCodeOverview(GroupEfficiencyRequestDto request) {
                String startDate = request.getStartDate().concat(" 00:00:00");
                String endDate = request.getEndDate().concat(" 23:59:59");
                request.setStartDate(startDate);
                request.setEndDate(endDate);
                TimePeriodInfo timePeriodInfo = TimeRange.getRangeTypeAndWeek(request);

                List<OrderCodeOverviewDto> orderCodeOverview = new ArrayList<>();

                List<OrderDetail> orderDetails = orderDetailRepository
                                .findByCreatedDateBetween(timePeriodInfo.getStartDate(), timePeriodInfo.getEndDate());
                for (OrderDetail orderDetail : orderDetails) {
                        OrderCodeOverviewDto dto = new OrderCodeOverviewDto();
                        dto.setOrderCode(orderDetail.getOrderCode());
                        dto.setPgTimeGoal(orderDetail.getPgTimeGoal());
                        Float totalPgTime = 0f;
                        List<DrawingCodeProcess> processes = orderDetail.getDrawingCodeProcesses();
                        if (processes != null) {
                                for (DrawingCodeProcess process : processes) {
                                        if (process.getProcessStatus() == 3 && process.getEndTime() != null
                                                        && process.getStartTime() != null) {
                                                ProcessTime processTime = process.getProcessTime() == null
                                                                ? processTimeService
                                                                                .calculateProcessTime(process)
                                                                : process.getProcessTime();
                                                totalPgTime += processTime.getPgTime();
                                        }
                                }
                        }
                        dto.setPgTime(totalPgTime);
                        dto.setDiffTime(orderDetail.getPgTimeGoal() - totalPgTime);
                        orderCodeOverview.add(dto);
                }
                return orderCodeOverview;
        }
}
