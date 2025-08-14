package com.example.Dynamo_Backend.exception;

public class ErrorResponse {
    private String code;
    private String message;
    private String path;
    private String timestamp;

    public ErrorResponse(String code, String message, String path) {
        this.code = code;
        this.message = message;
        this.path = path;
        this.timestamp = java.time.ZonedDateTime.now().toString();
    }
}
