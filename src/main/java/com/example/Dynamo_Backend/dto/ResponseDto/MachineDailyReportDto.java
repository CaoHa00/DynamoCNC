package com.example.Dynamo_Backend.dto.ResponseDto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class MachineDailyReportDto {
    private LocalDate logDate;
    private Integer machineId;
    private String shiftCode;
    private Float runPgSeconds;
    private Float runOffsetSeconds;
    private Float emptySeconds;
    private Float stopSeconds;
    private Float errorSeconds;

    private Float totalRun;

    // tính cho cột lấy từ machine segment
    private Float mainProductSeconds;
    private Float reRunSeconds;
    private Float lkSeconds;
    private Float electricSeconds;
    private Float preparationSeconds;
    //
    private Float spanSeconds;
    private Float expectedSeconds;
    // sản lượng
    private Integer quantity;
}
