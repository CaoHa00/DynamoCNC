package com.example.Dynamo_Backend.mapper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.example.Dynamo_Backend.dto.OperatorGroupDto;
import com.example.Dynamo_Backend.entities.OperatorGroup;

public class OperatorGroupMapper {
    public static OperatorGroup mapToOperatorGroup(OperatorGroupDto operatorGroupDto) {
        OperatorGroup operatorGroup = new OperatorGroup();
        operatorGroup.setOperatorGroupId(operatorGroupDto.getOperatorGroupId());
        operatorGroup.setGroup(operatorGroup.getGroup());
        operatorGroup.setOperator(operatorGroup.getOperator());
        // operatorGroup.setCreatedDate(operatorGroupDto.getCreatedDate());
        // operatorGroup.setUpdatedDate(operatorGroupDto.getUpdatedDate());
        return operatorGroup;
    }

    public static OperatorGroupDto mapToOperatorGroupDto(OperatorGroup operatorGroup) {
        OperatorGroupDto operatorGroupDto = new OperatorGroupDto();
        String formattedCreatedDate = Instant.ofEpochMilli(operatorGroup.getCreatedDate())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String formattedUpdatedDate = Instant.ofEpochMilli(operatorGroup.getCreatedDate())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        operatorGroupDto.setOperatorGroupId(operatorGroup.getOperatorGroupId());
        operatorGroupDto.setGroupId(operatorGroup.getGroup() != null ? operatorGroup.getGroup().getGroupId() : null);
        operatorGroupDto
                .setOperatorId(operatorGroup.getOperator() != null ? operatorGroup.getOperator().getId() : null);
        operatorGroupDto.setOperatorName(
                operatorGroup.getOperator() != null ? operatorGroup.getOperator().getOperatorName() : null);
        operatorGroupDto.setCreatedDate(formattedCreatedDate);
        operatorGroupDto.setUpdatedDate(formattedUpdatedDate);
        return operatorGroupDto;
    }

    public static OperatorGroupDto mapOperatorGroupStatusDto(OperatorGroup operatorGroup, int status) {
        OperatorGroupDto operatorGroupDto = new OperatorGroupDto();
        operatorGroupDto.setOperatorGroupId(operatorGroup.getOperatorGroupId());
        operatorGroupDto.setGroupId(operatorGroup.getGroup() != null ? operatorGroup.getGroup().getGroupId() : null);
        operatorGroupDto
                .setOperatorId(operatorGroup.getOperator() != null ? operatorGroup.getOperator().getId() : null);
        operatorGroupDto.setOperatorName(
                operatorGroup.getOperator() != null ? operatorGroup.getOperator().getOperatorName() : null);
        operatorGroupDto.setStatus(status);
        return operatorGroupDto;
    }
}
