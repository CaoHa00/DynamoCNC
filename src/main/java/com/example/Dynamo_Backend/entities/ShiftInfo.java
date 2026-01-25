package com.example.Dynamo_Backend.entities;

import java.time.LocalDate;

public class ShiftInfo {
    private String shiftCode;
    private LocalDate logDate;

    public ShiftInfo(String shiftCode, LocalDate logDate) {
        this.shiftCode = shiftCode;
        this.logDate = logDate;
    }

    public String getShiftCode() {
        return shiftCode;
    }

    public LocalDate getLogDate() {
        return logDate;
    }
}
