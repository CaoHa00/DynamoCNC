package com.example.Dynamo_Backend.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.QualityControlDto;
import com.example.Dynamo_Backend.entities.QualityControl;
import com.example.Dynamo_Backend.mapper.QualityControlMapper;
import com.example.Dynamo_Backend.repository.QualityControlRepository;
import com.example.Dynamo_Backend.service.QualityControlService;

@Service
public class QualityControlImplementation implements QualityControlService {
    QualityControlRepository qualityControlRepository;

    @Override
    public QualityControlDto addQualityControl(QualityControlDto qualityControlDto) {
        QualityControl qualityControl = QualityControlMapper.toQualityControl(qualityControlDto);
        long createdTimestamp = System.currentTimeMillis();
        qualityControl.setCreatedDate(createdTimestamp);
        qualityControl.setUpdatedDate(createdTimestamp);
        QualityControl saveQC = qualityControlRepository.save(qualityControl);
        return QualityControlMapper.toQualityControlDto(saveQC);
    }

    @Override
    public QualityControlDto updateQualityControl(String qualityControlId, QualityControlDto qualityControlDto) {
        QualityControl qualityControl = qualityControlRepository.findById(qualityControlId)
                .orElseThrow(() -> new RuntimeException("Can not find quality control " + qualityControlId));
        long updatedTimestamp = System.currentTimeMillis();

        qualityControl.setUpdatedDate(updatedTimestamp);
        qualityControl.setName(qualityControlDto.getName());
        qualityControl.setEmail(qualityControlDto.getEmail());
        qualityControl.setPassword(qualityControlDto.getPassword());
        qualityControl.setPhoneNumber(qualityControlDto.getPhoneNumber());

        QualityControl updatedQC = qualityControlRepository.save(qualityControl);
        return QualityControlMapper.toQualityControlDto(updatedQC);
    }

    @Override
    public QualityControlDto getQualityControlById(String qualityControlId) {
        QualityControl qualityControl = qualityControlRepository.findById(qualityControlId)
                .orElseThrow(() -> new RuntimeException("Can not find quality control " + qualityControlId));
        return QualityControlMapper.toQualityControlDto(qualityControl);
    }

    @Override
    public void deleteQualityControl(String qualityControlId) {
        QualityControl qualityControl = qualityControlRepository.findById(qualityControlId)
                .orElseThrow(() -> new RuntimeException("Can not find quality control " + qualityControlId));
        qualityControlRepository.delete(qualityControl);
    }

    @Override
    public List<QualityControlDto> getAllQualityControl() {
        List<QualityControl> qualityControls = qualityControlRepository.findAll();
        return qualityControls.stream().map(QualityControlMapper::toQualityControlDto).toList();
    }

}
