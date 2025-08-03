package com.example.Dynamo_Backend.event;

public class OperateHistoryMessageEvent {
    private final String payload;

    public OperateHistoryMessageEvent(String payload) {
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }
}
