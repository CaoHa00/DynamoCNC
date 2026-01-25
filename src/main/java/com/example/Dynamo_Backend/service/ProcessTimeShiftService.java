package com.example.Dynamo_Backend.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.entities.DrawingCodeProcess;
import com.example.Dynamo_Backend.entities.ProcessTime;
import com.example.Dynamo_Backend.entities.ProcessTimeShift;
import com.example.Dynamo_Backend.entities.ShiftInfo;
import com.example.Dynamo_Backend.repository.MachineSegmentRepository;
import com.example.Dynamo_Backend.repository.ProcessTimeShiftRepository;
import com.example.Dynamo_Backend.util.DateTimeUtil;
import com.example.Dynamo_Backend.util.ShiftUtil;
import com.example.Dynamo_Backend.util.TypeUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

// service này dùng để cập nhật cho ca và ngày để tính tổng thời gian dựa vào
// loại process
public class ProcessTimeShiftService {
    private final ProcessTimeShiftRepository processTimeShiftRepository;
    private final MachineDailyService machineDailyService;
    private final MachineSegmentRepository machineSegmentRepository;

    @Transactional
    public void calculateAfterProcessDone(
            ProcessTime processTime,
            DrawingCodeProcess process) {

        // Ca kết thúc
        ShiftInfo endShift = ShiftUtil.resolveShift(DateTimeUtil.convertLongToLocalDateTime(process.getEndTime()));

        // runtime (giây)
        long pgRuntime = Math.round(processTime.getPgTime() * 3600);

        // Lấy entity của ca kết thúc
        Integer machineId = process.getMachine().getMachineId();
        ProcessTimeShift entity = processTimeShiftRepository.findByMachineIdAndLogDateAndShiftCode(
                machineId,
                endShift.getLogDate(),
                endShift.getShiftCode());

        // Nếu chưa có thì tạo mới
        if (entity == null) {
            entity = new ProcessTimeShift();
            entity.setMachineId(machineId);
            entity.setLogDate(endShift.getLogDate());
            entity.setShiftCode(endShift.getShiftCode());
        }

        // Cộng toàn bộ runtime cho ca kết thúc
        String processType = process.getProcessType();
        switch (processType) {
            case "SP_Chính" ->
                entity.setMainProductPgSeconds(
                        entity.getMainProductPgSeconds() + pgRuntime);
            case "NG_Chạy lại", "LK-Đồ gá", "Dự bị" ->
                entity.setOtherPgSeconds(
                        entity.getOtherPgSeconds() + pgRuntime);
            case "Điện cực" ->
                entity.setElectricPgSeconds(
                        entity.getElectricPgSeconds() + pgRuntime);
        }
        long spanSeconds = entity.getSpanSeconds() + ((process.getEndTime() - process.getStartTime()) / 1000);
        entity.setSpanSeconds(spanSeconds);
        long expected = (long) (process.getPgTime() * 60) + entity.getExpectedSeconds();
        entity.setExpectedSeconds(expected);
        entity.setQuantity(
                entity.getQuantity() + 1);
        processTimeShiftRepository.save(entity);
    }

    @Transactional
    public void reCalculateProcess(
            DrawingCodeProcess process) {

        // Ca kết thúc
        ShiftInfo endShift = ShiftUtil.resolveShift(DateTimeUtil.convertLongToLocalDateTime(process.getEndTime()));

        // runtime (giây)

        LocalDateTime starTime = DateTimeUtil.convertLongToLocalDateTime(process.getStartTime());
        LocalDateTime endTime = DateTimeUtil.convertLongToLocalDateTime(process.getEndTime());
        List<Object[]> results = machineSegmentRepository.getGroupedDuration(
                process.getMachine().getMachineId(), starTime, endTime);

        HashMap<String, Float> activeTime = TypeUtil.buildStatusHourMap(results);
        long pgRuntime = Math.round(activeTime.get("R1") * 3600);

        // Lấy entity của ca kết thúc
        Integer machineId = process.getMachine().getMachineId();
        ProcessTimeShift entity = processTimeShiftRepository.findByMachineIdAndLogDateAndShiftCode(
                machineId,
                endShift.getLogDate(),
                endShift.getShiftCode());

        // Nếu chưa có thì tạo mới
        if (entity == null) {
            entity = new ProcessTimeShift();
            entity.setMachineId(machineId);
            entity.setLogDate(endShift.getLogDate());
            entity.setShiftCode(endShift.getShiftCode());
        }

        // Cộng toàn bộ runtime cho ca kết thúc
        String processType = process.getProcessType();
        switch (processType) {
            case "SP_Chính" ->
                entity.setMainProductPgSeconds(
                        entity.getMainProductPgSeconds() + pgRuntime);
            case "NG_Chạy lại", "LK-Đồ gá", "Dự bị" ->
                entity.setOtherPgSeconds(
                        entity.getOtherPgSeconds() + pgRuntime);
            case "Điện cực" ->
                entity.setElectricPgSeconds(
                        entity.getElectricPgSeconds() + pgRuntime);
        }
        long spanSeconds = entity.getSpanSeconds() + ((process.getEndTime() - process.getStartTime()) / 1000);
        entity.setSpanSeconds(spanSeconds);
        long expected = (long) (process.getPgTime() * 60) + entity.getExpectedSeconds();
        entity.setExpectedSeconds(expected);
        entity.setQuantity(
                entity.getQuantity() + 1);
        processTimeShiftRepository.save(entity);
    }

}
