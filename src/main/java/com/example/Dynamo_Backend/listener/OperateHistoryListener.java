package com.example.Dynamo_Backend.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.Dynamo_Backend.event.OperateHistoryMessageEvent;
import com.example.Dynamo_Backend.service.OperateHistoryService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OperateHistoryListener {

    private final OperateHistoryService operateHistoryService;

    @EventListener
    public void handleOperateHistoryMessage(OperateHistoryMessageEvent event) {
        operateHistoryService.addOperateHistory(event.getPayload());
    }
}
