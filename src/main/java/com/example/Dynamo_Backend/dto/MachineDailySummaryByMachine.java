package com.example.Dynamo_Backend.dto;

public record MachineDailySummaryByMachine(
        Long runPgSeconds,
        Long runOffsetSeconds,
        Long emptySeconds,
        Long stopSeconds,
        Long errorSeconds,

        // tính cho cột lấy từ machine segment
        Long mainProductSeconds,
        Long reRunSeconds,
        Long lkSeconds,
        Long electricSeconds,
        Long preparationSeconds,
        //
        Long mainProductPgSeconds,
        Long electricPgSeconds,
        Long otherSeconds,
        Long spanSeconds,
        Long expectedSeconds,
        // sản lượng
        Long quantity) {
}
