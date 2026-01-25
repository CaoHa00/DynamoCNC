package com.example.Dynamo_Backend.entities;

public class StatusInfo {
    private String status;
    private Long duration;

    public StatusInfo(String status, Long duration) {
        this.status = status;
        this.duration = duration;
    }
}
