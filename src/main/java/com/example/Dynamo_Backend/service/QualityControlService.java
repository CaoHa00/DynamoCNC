package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.QualityControlDto;

public interface QualityControlService {
    QualityControlDto addQualityControl(QualityControlDto qualityControlDto);

    QualityControlDto updateQualityControl(String qualityControlId, QualityControlDto qualityControlDto);

    QualityControlDto getQualityControlById(String qualityControlId);

    void deleteQualityControl(String qualityControlId);

    List<QualityControlDto> getAllQualityControl();
}
