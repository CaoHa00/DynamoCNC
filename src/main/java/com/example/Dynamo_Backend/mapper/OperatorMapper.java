package com.example.Dynamo_Backend.mapper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.example.Dynamo_Backend.dto.OperatorDto;
import com.example.Dynamo_Backend.entities.Operator;

public class OperatorMapper {
        public static Operator mapToOperator(OperatorDto operatorDto) {
                Operator operator = new Operator();
                operator.setId(operatorDto.getId());
                operator.setOperatorId(operatorDto.getOperatorId());
                operator.setOperatorName(operatorDto.getOperatorName());
                operator.setOperatorOffice(operatorDto.getOperatorOffice());
                operator.setOperatorSection(operatorDto.getOperatorSection());
                operator.setOperatorStep(operatorDto.getOperatorStep());
                operator.setKpi(operatorDto.getKpi());
                // operator.setStatus(operatorDto.getStatus());
                // operator.setDateAdd(operatorDto.getDateAdd());
                return operator;

        }

        public static OperatorDto mapToOperatorDto(Operator operator) {
                OperatorDto operatorDto = new OperatorDto();
                String formattedCreatedDate = Instant.ofEpochMilli(operator.getCreatedDate())
                                .atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String formattedUpdatedDate = Instant.ofEpochMilli(operator.getCreatedDate())
                                .atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                operatorDto.setId(operator.getId());
                operatorDto.setOperatorId(operator.getOperatorId());
                operatorDto.setOperatorName(operator.getOperatorName());
                operatorDto.setOperatorOffice(operator.getOperatorOffice());
                operatorDto.setOperatorStep(operator.getOperatorStep());
                operatorDto.setOperatorSection(operator.getOperatorSection());
                operatorDto.setKpi(operator.getKpi());
                operatorDto.setStatus(operator.getStatus());
                operatorDto.setCreatedDate(formattedCreatedDate);
                operatorDto.setUpdatedDate(formattedUpdatedDate);

                return operatorDto;
        }
}
