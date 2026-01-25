package com.example.Dynamo_Backend.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.entities.MachineSegment;
import com.example.Dynamo_Backend.entities.ShiftInfo;
import com.example.Dynamo_Backend.repository.MachineSegmentRepository;
import com.example.Dynamo_Backend.util.ShiftUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MachineSegmentServiceImplementation {

    private final MachineSegmentRepository segmentRepo;
    private final MachineDailyService dailyService;

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

            if (last.getEndTime() == null) {

                LocalDateTime cursor = last.getStartTime();

                // 🔁 CẮT QUA NHIỀU CA NẾU CÓ
                while (eventTime.isAfter(ShiftUtil.getShiftEnd(cursor))) {

                    LocalDateTime shiftEnd = ShiftUtil.getShiftEnd(cursor);

                    last.setEndTime(shiftEnd);
                    last.setDurationSeconds(
                            (int) Duration.between(last.getStartTime(), shiftEnd).getSeconds());
                    last.setAggregated(1);
                    savedClosedSegment(last);

                    // tạo segment carry
                    ShiftInfo carryShift = ShiftUtil.resolveShift(shiftEnd);

                    MachineSegment carry = new MachineSegment();
                    carry.setMachineId(machineId);
                    carry.setStatus(last.getStatus());
                    carry.setStartTime(shiftEnd);
                    carry.setShiftCode(carryShift.getShiftCode());
                    carry.setLogDate(carryShift.getLogDate());
                    carry.setProcessId(last.getProcessId());
                    carry.setProcessType(last.getProcessType());

                    last = carry;
                    cursor = shiftEnd;

                }

                // ĐÓNG SEGMENT TẠI EVENT
                last.setEndTime(eventTime);
                last.setDurationSeconds(
                        (int) Duration.between(last.getStartTime(), eventTime).getSeconds());
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
        dailyService.updateDailyTime(seg);
    }
}
