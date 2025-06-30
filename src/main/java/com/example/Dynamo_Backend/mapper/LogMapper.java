package com.example.Dynamo_Backend.mapper;

import com.example.Dynamo_Backend.dto.LogDto;
import com.example.Dynamo_Backend.entities.Log;

public class LogMapper {
    public static LogDto mapToStatsDto(Log stats) {
        return new LogDto(
                stats.getLogId(),
                stats.getTimeStamp(),
                stats.getStatus(),
                stats.getDrawingCodeProcess().getProcessId(),
                stats.getOperator().getId());
    }

    public static Log mapToStats(LogDto statsDto) {
        Log stats = new Log();
        stats.setLogId(statsDto.getLogId());
        stats.setTimeStamp(statsDto.getTimeStamp());

        stats.setStatus(statsDto.getStatus());
        // stats.setDrawingCodeProcess(drawingCodeProcess);
        // stats.setOperator(operator);

        return stats;
    }
}
