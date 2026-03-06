package com.example.Dynamo_Backend.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.entities.DrawingCodeProcess;
import com.example.Dynamo_Backend.entities.MachineSegment;
import com.example.Dynamo_Backend.entities.ShiftInfo;
import com.example.Dynamo_Backend.repository.DrawingCodeProcessRepository;
import com.example.Dynamo_Backend.repository.MachineDailyRepository;
import com.example.Dynamo_Backend.repository.MachineSegmentRepository;
import com.example.Dynamo_Backend.util.DateTimeUtil;
import com.example.Dynamo_Backend.util.ShiftUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MachineSegmentServiceImplementation {

    private final MachineSegmentRepository segmentRepo;
    private final MachineDailyService dailyService;
    private final DrawingCodeProcessRepository processRepository;
    private final MachineDailyRepository machineDailyRepository;
    // private final DrawingCodeProcessService processService;

    public void handleNewEvent(
            Integer machineId,
            String newStatus,
            LocalDateTime eventTime,
            String processId,
            String processType) {

        Optional<MachineSegment> lastOpt = segmentRepo.findTopByMachineIdOrderByStartTimeDesc(machineId);

        String lastStatus = null;

        if (lastOpt.isPresent()) {
            MachineSegment last = lastOpt.get();
            lastStatus = last.getStatus();
            // Nếu status không đổi → bỏ qua
            if (last.getEndTime() == null &&
                    Objects.equals(last.getStatus(), newStatus)) {
                return;
            }
            if (last.getEndTime() == null) {

                LocalDateTime cursor = last.getStartTime();

                // 🔁 CẮT QUA NHIỀU CA NẾU CÓ
                while (eventTime.isAfter(ShiftUtil.getShiftEnd(cursor))) {

                    LocalDateTime shiftEnd = ShiftUtil.getShiftEnd(cursor);
                    last.setEndTime(shiftEnd);
                    long ms = Duration.between(last.getStartTime(), shiftEnd).toMillis();
                    last.setDurationSeconds(
                            (int) Math.round(ms / 1000.0));
                    last.setAggregated(1);
                    savedClosedSegment(last);

                    // tạo segment carry
                    ShiftInfo carryShift = ShiftUtil.resolveShift(shiftEnd);

                    MachineSegment carry = new MachineSegment();
                    carry.setMachineId(machineId);
                    carry.setStatus(last.getStatus());
                    carry.setStartTime(last.getEndTime());
                    carry.setShiftCode(carryShift.getShiftCode());
                    carry.setLogDate(carryShift.getLogDate());
                    carry.setProcessId(last.getProcessId());
                    carry.setProcessType(last.getProcessType());

                    last = carry;
                    cursor = shiftEnd;
                }
                // ĐÓNG SEGMENT TẠI EVENT
                last.setEndTime(eventTime);
                long ms = Duration.between(last.getStartTime(), eventTime).toMillis();
                last.setDurationSeconds((int) Math.round(ms / 1000.0));
                last.setAggregated(1);
                savedClosedSegment(last);
            }
        }

        // CHỈ TẠO SEGMENT MỚI KHI STATUS THAY ĐỔI
        ShiftInfo newShift = ShiftUtil.resolveShift(eventTime);

        MachineSegment segment = new MachineSegment();
        segment.setMachineId(machineId);
        segment.setStatus(newStatus);
        segment.setStartTime(eventTime);
        segment.setShiftCode(newShift.getShiftCode());
        segment.setLogDate(newShift.getLogDate());

        if (!"0".equals(newStatus)) {
            segment.setProcessId(processId);
            segment.setProcessType(processType);
        }

        segmentRepo.save(segment);

    }

    public void handleNewEvent1(
            Integer machineId,
            String newStatus,
            LocalDateTime eventTime,
            String processId,
            String processType) {
        LocalDate lastDate = eventTime.toLocalDate().minusDays(1);
        Optional<MachineSegment> lastOpt = segmentRepo.findTopByMachineIdAndLogDateOrderByEndTimeDesc(machineId,
                lastDate);

        String lastStatus = null;

        if (lastOpt.isPresent()) {
            MachineSegment last = lastOpt.get();
            lastStatus = last.getStatus();
            // Nếu status không đổi → bỏ qua
            if (last.getEndTime() == null &&
                    Objects.equals(last.getStatus(), newStatus)) {
                return;
            }
            if (last.getEndTime() == null) {

                LocalDateTime cursor = last.getStartTime();

                // 🔁 CẮT QUA NHIỀU CA NẾU CÓ
                while (eventTime.isAfter(ShiftUtil.getShiftEnd(cursor))) {

                    LocalDateTime shiftEnd = ShiftUtil.getShiftEnd(cursor);
                    last.setEndTime(shiftEnd);
                    long ms = Duration.between(last.getStartTime(), shiftEnd).toMillis();
                    last.setDurationSeconds(
                            (int) Math.round(ms / 1000.0));
                    last.setAggregated(1);
                    savedClosedSegment(last);

                    // tạo segment carry
                    ShiftInfo carryShift = ShiftUtil.resolveShift(shiftEnd);

                    MachineSegment carry = new MachineSegment();
                    carry.setMachineId(machineId);
                    carry.setStatus(last.getStatus());
                    carry.setStartTime(last.getEndTime());
                    carry.setShiftCode(carryShift.getShiftCode());
                    carry.setLogDate(carryShift.getLogDate());
                    carry.setProcessId(last.getProcessId());
                    carry.setProcessType(last.getProcessType());

                    last = carry;
                    cursor = shiftEnd;
                }
                // ĐÓNG SEGMENT TẠI EVENT
                last.setEndTime(eventTime);
                long ms = Duration.between(last.getStartTime(), eventTime).toMillis();
                last.setDurationSeconds((int) Math.round(ms / 1000.0));
                last.setAggregated(1);
                savedClosedSegment(last);
            }
        }

        // CHỈ TẠO SEGMENT MỚI KHI STATUS THAY ĐỔI
        ShiftInfo newShift = ShiftUtil.resolveShift(eventTime);

        MachineSegment segment = new MachineSegment();
        segment.setMachineId(machineId);
        segment.setStatus(newStatus);
        segment.setStartTime(eventTime);
        segment.setShiftCode(newShift.getShiftCode());
        segment.setLogDate(newShift.getLogDate());

        if (!"0".equals(newStatus)) {
            segment.setProcessId(processId);
            segment.setProcessType(processType);
        }

        segmentRepo.save(segment);

    }

    private void savedClosedSegment(MachineSegment seg) {
        segmentRepo.save(seg);
        calcualateDurations(seg);
        dailyService.updateDailyTime(seg);
    }

    public void calcualateDurations(MachineSegment machineSegment) {
        if (machineSegment.getStatus().contains("R")) {
            DrawingCodeProcess process = processRepository
                    .findByMachine_MachineIdAndProcessStatus(machineSegment.getMachineId(), 2);
            if (process != null) {
                long duration = process.getDuration();
                duration += machineSegment.getDurationSeconds();
                process.setDuration(duration);
                processRepository.save(process);
            }
        }
    }

    @Transactional
    public void reRunMachineDailyBaseOnSegment(String date, List<Integer> machineIds) {
        for (Integer machineId : machineIds) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate start = LocalDate.parse(date, formatter);

            dailyService.updateDailyTime1(start, machineId);
        }

    }

}
