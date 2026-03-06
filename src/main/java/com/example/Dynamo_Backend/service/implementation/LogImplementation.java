package com.example.Dynamo_Backend.service.implementation;

import java.sql.Date;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import com.example.Dynamo_Backend.dto.LogDto;
import com.example.Dynamo_Backend.entities.CurrentStatus;
import com.example.Dynamo_Backend.entities.Staff;
import com.example.Dynamo_Backend.exception.ResourceNotFoundException;
import com.example.Dynamo_Backend.entities.Log;
import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.entities.MachineSegment;
import com.example.Dynamo_Backend.entities.ShiftInfo;
import com.example.Dynamo_Backend.mapper.LogMapper;
import com.example.Dynamo_Backend.repository.LogRepository;
import com.example.Dynamo_Backend.repository.MachineSegmentRepository;
import com.example.Dynamo_Backend.repository.OperateHistoryRepository;
import com.example.Dynamo_Backend.service.StaffService;
import com.example.Dynamo_Backend.util.DateTimeUtil;
import com.example.Dynamo_Backend.util.ShiftUtil;

import jakarta.transaction.Transactional;

import com.example.Dynamo_Backend.service.LogService;
import com.example.Dynamo_Backend.service.MachineSegmentServiceImplementation;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LogImplementation implements LogService {
        StaffService staffService;
        LogRepository logRepository;
        OperateHistoryRepository operateHistoryRepository;
        MachineSegmentServiceImplementation machineSegmentService;
        MachineSegmentRepository segmentRepo;

        @Override
        public void addLog(CurrentStatus currentStatus, Machine machine, Staff staff, Long timeStamp,
                        String processId) {
                Log log = new Log();
                log.setMachine(machine);
                log.setStaff(staff);
                log.setStatus(currentStatus.getStatus());
                log.setTimeStamp(timeStamp);
                log.setProcessId(processId);
                logRepository.save(log);

                // OperateHistory operateHistory = operateHistoryRepository
                // .findInProgressByMachineId(machine.getMachineId()).stream().findFirst().orElse(null);
                // if (operateHistory != null) {
                // Log lastLog = logRepository
                // .findTopByMachineIdAndTimeStampBeforeOrderByTimeStampDesc(
                // machine.getMachineId(),
                // log.getTimeStamp())
                // .stream().findFirst().orElse(null);
                // if (lastLog != null && "R1".equals(lastLog.getStatus())) {
                // long timeDiff = log.getTimeStamp() - lastLog.getTimeStamp();
                // Integer minutes = (int) timeDiff / (1000 * 60);
                // operateHistory.setPgTime(
                // operateHistory.getPgTime() != null
                // ? operateHistory.getPgTime() + minutes
                // : minutes);
                // operateHistoryRepository.save(operateHistory);
                // }
                // }
        }

        @Override
        public LogDto getLogById(String statsId) {
                Log stats = logRepository.findById(statsId)
                                .orElseThrow(() -> new ResourceNotFoundException("stats is not found:" + statsId));
                return LogMapper.mapToStatsDto(stats);
        }

        @Override
        public void deleteLog(String statsId) {
                Log stats = logRepository.findById(statsId)
                                .orElseThrow(() -> new ResourceNotFoundException("stats is not found:" + statsId));
                logRepository.delete(stats);

        }

        @Override
        public List<LogDto> getAllLog() {
                List<Log> statstistics = logRepository.findAll();
                return statstistics.stream().map(LogMapper::mapToStatsDto).toList();
        }

        @Transactional
        @Override
        public void reSegment(String startDate,

                        List<Integer> machineIds) {
                for (Integer machineId : machineIds) {

                        // ===============================
                        // 1️⃣ Xác định mốc 07:00 → 07:00 hôm sau
                        // ===============================

                        LocalDateTime startTime = LocalDate.parse(startDate).atTime(7, 0);

                        LocalDateTime endTime = LocalDate.parse(startDate).plusDays(1).atTime(7, 0);

                        Long startTs = DateTimeUtil.convertLocalDateTimeToLong(startTime);
                        Long endTs = DateTimeUtil.convertLocalDateTimeToLong(endTime);

                        // ===============================
                        // 2️⃣ Xóa segment trong khoảng rebuild
                        // ===============================

                        segmentRepo.deleteByMachineIdAndStartTimeBetween(
                                        machineId,
                                        startTime,
                                        endTime);

                        // ===============================
                        // 3️⃣ Lấy segment gần nhất trước 07:00
                        // ===============================

                        MachineSegment previous = segmentRepo.findTopByMachineIdAndStartTimeBeforeOrderByStartTimeDesc(
                                        machineId,
                                        startTime);

                        MachineSegment currentSegment = null;
                        String currentStatus = null;

                        if (previous != null) {

                                currentStatus = previous.getStatus();

                                // Nếu segment hôm trước chưa đóng hoặc đóng sau 07:00
                                if (previous.getEndTime() == null
                                                || previous.getEndTime().isAfter(startTime)) {

                                        previous.setEndTime(startTime);

                                        long seconds = Math.max(0,
                                                        Duration.between(
                                                                        previous.getStartTime(),
                                                                        startTime).toMillis());

                                        previous.setDurationSeconds((int) Math.round(seconds / 1000.0));
                                        previous.setAggregated(1);

                                        segmentRepo.save(previous);
                                }

                                // 🔥 Tạo carry segment đúng 07:00
                                currentSegment = createSegment(
                                                machineId,
                                                currentStatus,
                                                startTime);

                                currentStatus = previous.getStatus();
                        }

                        // ===============================
                        // 4️⃣ Load log tăng dần
                        // ===============================

                        List<Log> logs = logRepository
                                        .findByMachine_machineIdAndTimeStampBetweenOrderByTimeStampAsc(
                                                        machineId,
                                                        startTs,
                                                        endTs);

                        for (Log log : logs) {

                                String newStatus = log.getStatus();
                                LocalDateTime eventTime = DateTimeUtil.convertLongToLocalDateTime(
                                                log.getTimeStamp());

                                // Nếu chưa có segment nền
                                if (currentSegment == null) {

                                        currentSegment = createSegment(
                                                        machineId,
                                                        newStatus,
                                                        eventTime);

                                        currentStatus = newStatus;
                                        continue;
                                }

                                // Nếu status không đổi
                                if (Objects.equals(currentStatus, newStatus)) {
                                        continue;
                                }

                                // ===============================
                                // 🔥 CẮT CA nếu cần (dùng ShiftUtil)
                                // ===============================

                                while (ShiftUtil.getShiftEnd(
                                                currentSegment.getStartTime()).isBefore(eventTime)) {

                                        LocalDateTime shiftEnd = ShiftUtil.getShiftEnd(
                                                        currentSegment.getStartTime());

                                        closeSegment(currentSegment, shiftEnd);
                                        segmentRepo.save(currentSegment);

                                        currentSegment = createSegment(
                                                        machineId,
                                                        currentStatus,
                                                        shiftEnd);
                                }

                                // ===============================
                                // Đóng tại event
                                // ===============================

                                closeSegment(currentSegment, eventTime);
                                segmentRepo.save(currentSegment);

                                // Tạo segment mới
                                currentSegment = createSegment(
                                                machineId,
                                                newStatus,
                                                eventTime);

                                currentStatus = newStatus;
                        }

                        // ===============================
                        // 5️⃣ Đóng segment cuối tại endTime
                        // ===============================

                        if (currentSegment != null) {

                                while (ShiftUtil.getShiftEnd(
                                                currentSegment.getStartTime()).isBefore(endTime)) {

                                        LocalDateTime shiftEnd = ShiftUtil.getShiftEnd(
                                                        currentSegment.getStartTime());

                                        closeSegment(currentSegment, shiftEnd);
                                        segmentRepo.save(currentSegment);

                                        currentSegment = createSegment(
                                                        machineId,
                                                        currentStatus,
                                                        shiftEnd);
                                }

                                closeSegment(currentSegment, endTime);
                                segmentRepo.save(currentSegment);
                        }
                }

        }

        private MachineSegment createSegment(Integer machineId,
                        String status,
                        LocalDateTime startTime) {

                ShiftInfo shiftInfo = ShiftUtil.resolveShift(startTime);

                MachineSegment segment = new MachineSegment();
                segment.setMachineId(machineId);
                segment.setStatus(status);
                segment.setStartTime(startTime);
                segment.setShiftCode(shiftInfo.getShiftCode());
                segment.setLogDate(shiftInfo.getLogDate());
                segment.setAggregated(1);

                return segmentRepo.save(segment);
        }

        private void closeSegment(
                        MachineSegment segment,
                        LocalDateTime endTime) {

                segment.setEndTime(endTime);

                long seconds = Duration.between(
                                segment.getStartTime(),
                                endTime).toMillis();

                segment.setDurationSeconds((int) Math.round(seconds / 1000.0));

                segment.setAggregated(1); // đã đóng
        }

}
