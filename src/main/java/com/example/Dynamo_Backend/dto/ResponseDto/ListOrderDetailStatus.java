package com.example.Dynamo_Backend.dto.ResponseDto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ListOrderDetailStatus {
    String orderDetailId;
    String orderCode;
    List<OrderDetailStatus> orderStatus = new ArrayList<>();
}
