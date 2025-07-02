package com.example.Dynamo_Backend.service.implementation;

import java.util.List;

import com.example.Dynamo_Backend.dto.QuantityControlDto;
import com.example.Dynamo_Backend.entities.QuantityControl;
import com.example.Dynamo_Backend.mapper.QuantityControlMapper;
import com.example.Dynamo_Backend.repository.QuantityControlRepository;
import com.example.Dynamo_Backend.service.QuantityControlService;

public class QuantityControlImplementation implements QuantityControlService {
    QuantityControlRepository quantityControlRepository;

    @Override
    public QuantityControlDto addQuantityControl(QuantityControlDto quantityControlDto) {
        QuantityControl quantityControl = QuantityControlMapper.toQuantityControl(quantityControlDto);
        long createdTimestamp = System.currentTimeMillis();
        quantityControl.setCreatedDate(createdTimestamp);
        quantityControl.setUpdatedDate(createdTimestamp);
        QuantityControl saveQC = quantityControlRepository.save(quantityControl);
        return QuantityControlMapper.toQuantityControlDto(saveQC);
    }

    @Override
    public QuantityControlDto updateQuantityControl(String quantityControlId, QuantityControlDto quantityControlDto) {
        QuantityControl quantityControl = quantityControlRepository.findById(quantityControlId)
                .orElseThrow(() -> new RuntimeException("Can not find quantity control " + quantityControlId));
        long updatedTimestamp = System.currentTimeMillis();

        quantityControl.setUpdatedDate(updatedTimestamp);
        quantityControl.setName(quantityControlDto.getName());
        quantityControl.setEmail(quantityControlDto.getEmail());
        quantityControl.setPassword(quantityControlDto.getPassword());
        quantityControl.setPhoneNumber(quantityControlDto.getPhoneNumber());

        QuantityControl updatedQC = quantityControlRepository.save(quantityControl);
        return QuantityControlMapper.toQuantityControlDto(updatedQC);
    }

    @Override
    public QuantityControlDto getQuantityControlById(String quantityControlId) {
        QuantityControl quantityControl = quantityControlRepository.findById(quantityControlId)
                .orElseThrow(() -> new RuntimeException("Can not find quantity control " + quantityControlId));
        return QuantityControlMapper.toQuantityControlDto(quantityControl);
    }

    @Override
    public void deleteQuantityControl(String quantityControlId) {
        QuantityControl quantityControl = quantityControlRepository.findById(quantityControlId)
                .orElseThrow(() -> new RuntimeException("Can not find quantity control " + quantityControlId));
        quantityControlRepository.delete(quantityControl);
    }

    @Override
    public List<QuantityControlDto> getAllQuantityControl() {
        List<QuantityControl> quantityControls = quantityControlRepository.findAll();
        return quantityControls.stream().map(QuantityControlMapper::toQuantityControlDto).toList();
    }

}
