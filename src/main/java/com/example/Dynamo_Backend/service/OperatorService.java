package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.OperatorDto;

public interface OperatorService {
    OperatorDto addOperator(OperatorDto operatorDto);

    OperatorDto updateOperator(String Id, OperatorDto operatorDto);

    void deleteOperator(List<String> ids);

    List<OperatorDto> getAllOperators();

    OperatorDto getOperatorById(String Id);

}
