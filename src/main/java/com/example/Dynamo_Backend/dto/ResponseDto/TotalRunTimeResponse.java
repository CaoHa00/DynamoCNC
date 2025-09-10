package com.example.Dynamo_Backend.dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TotalRunTimeResponse {
    private Float runTimeOfMainProduct; // in hours
    private Float runTimeOfRerun;
    private Float runTimeOfLK;
    private Float runTimeOfElectric;
    private Float totalRunTimeOfPreparation;
    private Float totalPgTime;
    private Float totalOffsetTime;
    private Float totalStopTime;
    private Float totalErrorTime;
}
