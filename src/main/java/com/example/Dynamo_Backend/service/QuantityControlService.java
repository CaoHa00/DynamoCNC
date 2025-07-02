package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.QuantityControlDto;

public interface QuantityControlService {
    QuantityControlDto addQuantityControl(QuantityControlDto quantityControlDto);

    QuantityControlDto updateQuantityControl(String quantityControlId, QuantityControlDto quantityControlDto);

    QuantityControlDto getQuantityControlById(String quantityControlId);

    void deleteQuantityControl(String quantityControlId);

    List<QuantityControlDto> getAllQuantityControl();
}
