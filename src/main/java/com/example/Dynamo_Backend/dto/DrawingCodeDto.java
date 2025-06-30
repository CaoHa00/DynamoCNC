package com.example.Dynamo_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DrawingCodeDto {
    private String drawingCodeId;
    private String drawingCodeName;
    private String addDate;
    private int status;
    private String createdDate;
    private String updatedDate;
    // private List<Order> orders;
    // private List<DrawingCodeProcess> drawingCodeProcesses;
}
