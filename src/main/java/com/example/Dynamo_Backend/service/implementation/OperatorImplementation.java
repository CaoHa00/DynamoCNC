package com.example.Dynamo_Backend.service.implementation;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.OperatorDto;
import com.example.Dynamo_Backend.entities.Operator;
import com.example.Dynamo_Backend.mapper.OperatorMapper;
import com.example.Dynamo_Backend.repository.OperatorRepository;
import com.example.Dynamo_Backend.service.OperatorService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OperatorImplementation implements OperatorService {
    @Autowired
    OperatorRepository operatorRepository;

    @Override
    public OperatorDto addOperator(OperatorDto operatorDto) {
        Operator operator = OperatorMapper.mapToOperator(operatorDto);
        long createdTimestamp = System.currentTimeMillis();
        int status = 1;
        operator.setCreatedDate(createdTimestamp);
        operator.setUpdatedDate(createdTimestamp);
        operator.setStatus(status);
        Operator savOperator = operatorRepository.save(operator);
        return OperatorMapper.mapToOperatorDto(savOperator);
    }

    @Override
    public void deleteOperator(List<String> ids) {
        for (String id : ids) {
            Operator operator = operatorRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Operator is not found:" + id));
            operatorRepository.delete(operator);
        }
    }

    @Override
    public List<OperatorDto> getAllOperators() {
        List<Operator> operators = operatorRepository.findAll();
        return operators.stream().map(OperatorMapper::mapToOperatorDto).toList();
    }

    @Override
    public OperatorDto getOperatorById(String Id) {
        Operator operator = operatorRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Operator is not found:" + Id));
        return OperatorMapper.mapToOperatorDto(operator);
    }

    @Override
    public OperatorDto updateOperator(String Id, OperatorDto operatorDto) {
        Operator operator = operatorRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Operator is not found:" + Id));

        long updatedTimestamp = System.currentTimeMillis();
        operator.setOperatorName(operatorDto.getOperatorName());
        operator.setOperatorId(operatorDto.getOperatorId());
        operator.setOperatorOffice(operatorDto.getOperatorOffice());
        operator.setOperatorSection(operatorDto.getOperatorSection());
        operator.setOperatorStep(operatorDto.getOperatorStep());
        operator.setKpi(operatorDto.getKpi());
        operator.setStatus(operatorDto.getStatus());
        operator.setCreatedDate(operatorDto.getCreatedDate());
        operator.setUpdatedDate(updatedTimestamp);

        Operator updatedOperator = operatorRepository.save(operator);
        return OperatorMapper.mapToOperatorDto(updatedOperator);
    }
}
