package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.OperatorGroupDto;

public interface OperatorGroupService {

    OperatorGroupDto addOperatorGroup(OperatorGroupDto operatorGroupDto);

    OperatorGroupDto updateOperatorGroup(String Id, OperatorGroupDto operatorGroupDtoDto);

    OperatorGroupDto getOperatorGroupById(String Id);

    void deleteOperatorGroup(String Id);

    List<OperatorGroupDto> getOperatorGroups();
}
