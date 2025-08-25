package com.example.Dynamo_Backend.dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class DrawingCodeProcessStatistic {
    private Integer numberOfProcess;
    private Float totalRunTime;
    private Float totalPgTime;
    private Float pgTimeDiffRate;// compare with pg time goal
}
