package com.example.Dynamo_Backend.service.implementation;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

import com.example.Dynamo_Backend.entities.CurrentStatus;
import com.example.Dynamo_Backend.entities.MachineSegment;
import com.example.Dynamo_Backend.entities.ShiftType;
import com.example.Dynamo_Backend.repository.CurrentStatusRepository;
import com.example.Dynamo_Backend.repository.MachineSegmentRepository;
import com.example.Dynamo_Backend.service.MachineSegmentService;

public class MachineSegmentImplementation implements MachineSegmentService {
    MachineSegmentRepository machineSegmentRepository;

    @Override
    public MachineSegment updateSegemnt() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateSegemnt'");
    }

    @Override
    public void addNewSegmet(String payload, String previousStatus, Long previousTime, Long currentTime) {
        String[] arr = payload.split("-");
        int machineId = Integer.parseInt(arr[0]) + 1;

        ZoneId zoneId = ZoneId.systemDefault();

        LocalDateTime start = Instant.ofEpochMilli(previousTime)
                .atZone(zoneId)
                .toLocalDateTime();

        LocalDateTime end = Instant.ofEpochMilli(currentTime)
                .atZone(zoneId)
                .toLocalDateTime();

        while (start.isBefore(end)) {

            ShiftType shift = ShiftType.from(start);
            LocalDateTime shiftEnd;

            if (shift == ShiftType.DAY) {
                // ca ngày: 07:00 → 19:00
                shiftEnd = start.toLocalDate()
                        .atTime(19, 0);
            } else {
                // ca đêm: 19:00 → 07:00 hôm sau
                LocalDate date = start.toLocalDate();
                if (start.toLocalTime().isBefore(LocalTime.of(7, 0))) {
                    // đang là ca đêm của ngày hôm trước
                    shiftEnd = date.atTime(7, 0);
                } else {
                    shiftEnd = date.plusDays(1).atTime(7, 0);
                }
            }

            LocalDateTime sliceEnd = end.isBefore(shiftEnd) ? end : shiftEnd;
            long durationSeconds = Duration.between(start, sliceEnd).getSeconds();

            MachineSegment segment = new MachineSegment();
            segment.setMachineId(machineId);
            segment.setStatus(previousStatus);
            segment.setStartTime(start.atZone(zoneId).toInstant().toEpochMilli());
            segment.setEndTime(sliceEnd.atZone(zoneId).toInstant().toEpochMilli());
            segment.setDuration(durationSeconds);
            segment.setShift(shift.name());

            machineSegmentRepository.save(segment);

            start = sliceEnd;
        }

    }

}
