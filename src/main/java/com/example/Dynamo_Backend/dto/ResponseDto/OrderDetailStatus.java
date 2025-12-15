package com.example.Dynamo_Backend.dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderDetailStatus {
    Integer partNumber;
    Integer totalStep;
    Integer doneStep;
    Integer doingStep;
}
