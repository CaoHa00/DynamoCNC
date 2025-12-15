package com.example.Dynamo_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDto {
    private String orderDetailId;
    private String orderCode;
    private Integer quantity;
    private String orderType;
    private String createdDate;
    private String updatedDate;
    private Integer pgTimeGoal;
    private String managerGroupId;
    private Integer numberOfSteps;
    private String office;
    private Integer progress;
    // private int status;

    public OrderDetailDto(String orderDetailId, String orderCode) {
        this.orderDetailId = orderDetailId;
        this.orderCode = orderCode;
    }

}
