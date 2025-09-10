package com.example.Dynamo_Backend.mapper;

import com.example.Dynamo_Backend.dto.LogDto;
import com.example.Dynamo_Backend.entities.Log;

public class LogMapper {
    public static LogDto mapToStatsDto(Log stats) {
        String productStatus = "";
        String machineStatus = "";

        if (stats.getStatus().length() < 3) {
            // báo lỗi
        }
        String productStatusCode = stats.getStatus().substring(0, 1);
        String machineStatusCode = stats.getStatus().substring(1);

        switch (productStatusCode) {
            case "0":
                productStatus = "NG chạy lại";
                break;
            case "1":
                productStatus = "Chạy SP chính";
                break;
            case "2":
                productStatus = "Chạy SP phụ";
                break;
            default:
                productStatus = "Trạng thái sản phẩm";
                break;
        }

        switch (machineStatusCode) {
            case "R1":
                machineStatus = " Chạy mới";
                break;
            case "R2":
                machineStatus = " Chạy lặp lại";
                break;
            case "E0":
                machineStatus = " Máy chạy bình thường";
                break;
            case "E1":
                machineStatus = " Lỗi máy";
                break;
            case "E2":
                machineStatus = " Lỗi người vận hành";
                break;
            case "S1":
                machineStatus = " Dừng trong sản phẩm";
                break;
            case "S2":
                machineStatus = " Dừng máy trống";
                break;
            default:
                machineStatus = " Trạng thái máy";
                break;
        }

        String status = productStatus.concat(machineStatus);
        return new LogDto(
                stats.getLogId(),
                stats.getTimeStamp(),
                status,
                // stats.getDrawingCodeProcess().getProcessId(),
                stats.getMachine().getMachineId(),
                stats.getStaff().getId());
    }

    // chuye data
    public static Log mapToStats(LogDto statsDto) {
        Log stats = new Log();
        stats.setLogId(statsDto.getLogId());
        stats.setTimeStamp(statsDto.getTimeStamp());

        stats.setStatus(statsDto.getStatus());
        // stats.setDrawingCodeProcess(drawingCodeProcess);
        // stats.setStaff(staff);

        return stats;
    }
}
