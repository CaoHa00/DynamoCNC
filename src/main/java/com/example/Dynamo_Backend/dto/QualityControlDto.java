package com.example.Dynamo_Backend.dto;

import java.util.List;

import com.example.Dynamo_Backend.entities.DrawingCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class QualityControlDto {
    private String Id;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private String qcNote;
    private String status;
    private String createdDate;
    private String updatedDate;
    private List<DrawingCode> drawingCodes;
}
