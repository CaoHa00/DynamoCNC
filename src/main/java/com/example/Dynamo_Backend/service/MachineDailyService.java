package com.example.Dynamo_Backend.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.entities.MachineDaily;
import com.example.Dynamo_Backend.entities.MachineSegment;
import com.example.Dynamo_Backend.entities.ProcessTimeShift;
import com.example.Dynamo_Backend.repository.MachineDailyRepository;
import com.example.Dynamo_Backend.repository.MachineSegmentRepository;
import com.example.Dynamo_Backend.repository.ProcessTimeShiftRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MachineDailyService {

        private final MachineSegmentRepository segmentRepo;
        private final MachineDailyRepository dailyRepo;
        private final ProcessTimeShiftRepository processRepo;

        public void updateDailyTime(MachineSegment seg) {

                Integer machineId = seg.getMachineId();
                LocalDate logDate = seg.getLogDate();
                String shiftCode = seg.getShiftCode();

                MachineDaily daily = dailyRepo
                                .findByMachineIdAndLogDateAndShiftCode(
                                                machineId, logDate, shiftCode)
                                .orElseGet(() -> {
                                        MachineDaily d = new MachineDaily();
                                        d.setMachineId(machineId);
                                        d.setLogDate(logDate);
                                        d.setShiftCode(shiftCode);
                                        d.setRunPgSeconds(0L);
                                        d.setRunOffsetSeconds(0L);
                                        d.setStopSeconds(0L);
                                        d.setErrorSeconds(0L);
                                        d.setEmptySeconds(0L);
                                        d.setMainProductSeconds(0L);
                                        d.setReRunSeconds(0l);
                                        d.setLkSeconds(0l);
                                        d.setElectricSeconds(0l);
                                        d.setPreparationSeconds(0l);
                                        d.setMainProductPgSeconds(0l);
                                        d.setElectricPgSeconds(0l);
                                        d.setOtherSeconds(0l);
                                        d.setSpanSeconds(0L);
                                        d.setExpectedSeconds(0l);
                                        d.setQuantity(0);
                                        return d;
                                });

                long duration = seg.getDurationSeconds();

                switch (seg.getStatus()) {
                        case "R1" -> daily.setRunPgSeconds(
                                        daily.getRunPgSeconds() + duration);
                        case "R2" -> daily.setRunOffsetSeconds(
                                        daily.getRunOffsetSeconds() + duration);
                        case "S1" -> daily.setStopSeconds(
                                        daily.getStopSeconds() + duration);
                        case "S2" -> daily.setStopSeconds(
                                        daily.getStopSeconds() + duration);
                        case "E1" -> daily.setErrorSeconds(
                                        daily.getErrorSeconds() + duration);
                        case "E2" -> daily.setErrorSeconds(
                                        daily.getErrorSeconds() + duration);
                        case "E3" -> daily.setErrorSeconds(
                                        daily.getErrorSeconds() + duration);
                        case "0" -> daily.setEmptySeconds(
                                        daily.getEmptySeconds() + duration);
                }
                if (seg.getProcessType() != null && seg.getStatus().contains("R")) {
                        switch (seg.getProcessType()) {
                                case "SP_Chính" -> daily.setMainProductSeconds(
                                                daily.getMainProductSeconds() + duration);
                                case "NG_Chạy lại" -> daily.setReRunSeconds(
                                                daily.getReRunSeconds() + duration);
                                case "LK-Đồ gá" -> daily.setLkSeconds(
                                                daily.getLkSeconds() + duration);
                                case "Điện cực" -> daily.setElectricSeconds(
                                                daily.getElectricSeconds() + duration);
                                case "Dự bị" -> daily.setPreparationSeconds(
                                                daily.getPreparationSeconds() + duration);
                        }
                }
                dailyRepo.save(daily);
                seg.setAggregated(1);
                segmentRepo.save(seg);
        }

        @Transactional
        public void updateDailyQuantity(ProcessTimeShift processTimeShift) {

                Integer machineId = processTimeShift.getMachineId();
                LocalDate logDate = processTimeShift.getLogDate();
                String shiftCode = processTimeShift.getShiftCode();

                MachineDaily daily = dailyRepo
                                .findByMachineIdAndLogDateAndShiftCode(
                                                machineId, logDate, shiftCode)
                                .orElseGet(() -> {
                                        MachineDaily d = new MachineDaily();
                                        d.setMachineId(machineId);
                                        d.setLogDate(logDate);
                                        d.setShiftCode(shiftCode);
                                        d.setRunPgSeconds(0L);
                                        d.setRunOffsetSeconds(0L);
                                        d.setStopSeconds(0L);
                                        d.setErrorSeconds(0L);
                                        d.setEmptySeconds(0L);
                                        d.setMainProductSeconds(0L);
                                        d.setReRunSeconds(0l);
                                        d.setLkSeconds(0l);
                                        d.setElectricSeconds(0l);
                                        d.setPreparationSeconds(0l);
                                        d.setMainProductPgSeconds(0l);
                                        d.setElectricSeconds(0l);
                                        d.setOtherSeconds(0l);
                                        d.setSpanSeconds(0l);
                                        d.setExpectedSeconds(0l);
                                        d.setQuantity(0);
                                        return d;
                                });
                // cho hiệu suất
                daily.setMainProductPgSeconds(processTimeShift.getMainProductPgSeconds());
                daily.setElectricPgSeconds(processTimeShift.getElectricPgSeconds());
                Long others = processTimeShift.getOtherPgSeconds();
                daily.setOtherSeconds(others);
                daily.setExpectedSeconds(processTimeShift.getExpectedSeconds());
                daily.setSpanSeconds(processTimeShift.getSpanSeconds());
                daily.setQuantity(processTimeShift.getQuantity());
                dailyRepo.save(daily);
        }

        @Transactional
        public void updateDaily() {
                List<MachineDaily> daily = dailyRepo.findAll();
                for (MachineDaily machineDaily : daily) {
                        ProcessTimeShift processTimeShift = processRepo.findByMachineIdAndLogDateAndShiftCode(
                                        machineDaily.getMachineId(), machineDaily.getLogDate(),
                                        machineDaily.getShiftCode());

                        // cho hiệu suất
                        if (processTimeShift != null) {
                                machineDaily.setMainProductPgSeconds(processTimeShift.getMainProductPgSeconds());
                                machineDaily.setElectricPgSeconds(processTimeShift.getElectricPgSeconds());
                                Long others = processTimeShift.getOtherPgSeconds();
                                machineDaily.setOtherSeconds(others);
                                machineDaily.setExpectedSeconds(processTimeShift.getExpectedSeconds());
                                machineDaily.setSpanSeconds(processTimeShift.getSpanSeconds());
                                machineDaily.setQuantity(processTimeShift.getQuantity());
                                dailyRepo.save(machineDaily);
                        }

                }

        }

}
