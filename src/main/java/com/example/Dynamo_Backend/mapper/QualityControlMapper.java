package com.example.Dynamo_Backend.mapper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.example.Dynamo_Backend.dto.QualityControlDto;
import com.example.Dynamo_Backend.entities.QualityControl;

public class QualityControlMapper {
    public static QualityControlDto toQualityControlDto(QualityControl qualityControl) {
        QualityControlDto qualityControlDto = new QualityControlDto();
        String formattedCreatedDate = Instant.ofEpochMilli(qualityControl.getCreatedDate())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String formattedUpdatedDate = Instant.ofEpochMilli(qualityControl.getCreatedDate())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        qualityControlDto.setId(qualityControl.getId());
        qualityControlDto.setName(qualityControl.getName());
        qualityControlDto.setCreatedDate(formattedCreatedDate);
        qualityControlDto.setUpdatedDate(formattedUpdatedDate);
        qualityControlDto.setEmail(qualityControl.getEmail());
        qualityControlDto.setPassword(qualityControl.getPassword());
        qualityControlDto.setPhoneNumber(qualityControl.getPhoneNumber());
        if (qualityControl.isStatus()) {
            qualityControlDto.setStatus("Active");
        } else {
            qualityControlDto.setStatus("Inactive");
        }
        return qualityControlDto;
    }

    public static QualityControl toQualityControl(QualityControlDto qualityControlDto) {
        QualityControl qualityControl = new QualityControl();
        qualityControl.setId(qualityControlDto.getId());
        qualityControl.setName(qualityControlDto.getName());
        qualityControl.setEmail(qualityControlDto.getEmail());
        qualityControl.setPassword(qualityControlDto.getPassword());
        qualityControl.setPhoneNumber(qualityControlDto.getPhoneNumber());
        if (qualityControlDto.getStatus() == "Active") {
            qualityControl.setStatus(true);
        } else {
            qualityControl.setStatus(false);
        }

        return qualityControl;
    }
}
