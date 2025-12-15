package com.example.Dynamo_Backend.dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PartProgressDto {
    private String orderDetailId;
    private String orderCode;
    private Integer partNumber;
    private Long totalStep;
    private Long doneStep;
    private Long doingStep;
}
