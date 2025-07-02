package com.example.Dynamo_Backend.mapper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.example.Dynamo_Backend.dto.QuantityControlDto;
import com.example.Dynamo_Backend.entities.QuantityControl;

public class QuantityControlMapper {
    public static QuantityControlDto toQuantityControlDto(QuantityControl quantityControl) {
        QuantityControlDto quantityControlDto = new QuantityControlDto();
        String formattedCreatedDate = Instant.ofEpochMilli(quantityControl.getCreatedDate())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String formattedUpdatedDate = Instant.ofEpochMilli(quantityControl.getCreatedDate())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        quantityControlDto.setId(quantityControl.getId());
        quantityControlDto.setName(quantityControl.getName());
        quantityControlDto.setCreatedDate(formattedCreatedDate);
        quantityControlDto.setUpdatedDate(formattedUpdatedDate);
        quantityControlDto.setEmail(quantityControl.getEmail());
        quantityControlDto.setPassword(quantityControl.getPassword());
        quantityControlDto.setPhoneNumber(quantityControl.getPhoneNumber());
        if (quantityControl.isStatus()) {
            quantityControlDto.setStatus("Active");
        } else {
            quantityControlDto.setStatus("Inactive");
        }
        return quantityControlDto;
    }

    public static QuantityControl toQuantityControl(QuantityControlDto quantityControlDto) {
        QuantityControl quantityControl = new QuantityControl();
        quantityControl.setId(quantityControlDto.getId());
        quantityControl.setName(quantityControlDto.getName());
        quantityControl.setEmail(quantityControlDto.getEmail());
        quantityControl.setPassword(quantityControlDto.getPassword());
        quantityControl.setPhoneNumber(quantityControlDto.getPhoneNumber());
        if (quantityControlDto.getStatus() == "Active") {
            quantityControl.setStatus(true);
        } else {
            quantityControl.setStatus(false);
        }

        return quantityControl;
    }
}
