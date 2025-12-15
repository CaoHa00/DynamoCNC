package com.example.Dynamo_Backend.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.Dynamo_Backend.dto.CurrentStatusDto;
import com.example.Dynamo_Backend.dto.ResponseDto.CurrentStatusResponseDto;
import com.example.Dynamo_Backend.dto.ResponseDto.ListCurrentStaffStatusDto;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MyWebSocketHandler extends TextWebSocketHandler {

    private static final List<WebSocketSession> sessions = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        sessions.add(session);
        session.sendMessage(new TextMessage("{\"message\":\"Connected to WebSocket server\"}"));
        System.out.println("New WebSocket client connected.");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        // Broadcast message to all clients
        for (WebSocketSession webSocketSession : sessions) {
            if (webSocketSession.isOpen()) {
                webSocketSession.sendMessage(message);
            }
        }
    }

    public static void sendMessageToClients(String message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonMessage = objectMapper.writeValueAsString(
                new java.util.HashMap<String, Object>() {
                    {
                        put("type", "mqtt");
                        put("message", message);
                    }
                });
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(jsonMessage));
            }
        }
    }

    public static void sendGroupStatusToClients(String message) throws IOException {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        }
    }

    public static void sendMachineStatusByGroupToClients(List<CurrentStatusResponseDto> currentStatusDtos)
            throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonMessage = objectMapper.writeValueAsString(
                new java.util.HashMap<String, Object>() {
                    {
                        put("type", "status");
                        put("data", currentStatusDtos);
                    }
                });
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(jsonMessage));
            }
        }
    }

    public static void sendMachineStatusToClients(List<CurrentStatusDto> currentStatusDtos) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonMessage = objectMapper.writeValueAsString(
                new java.util.HashMap<String, Object>() {
                    {
                        put("type", "status");
                        put("data", currentStatusDtos);
                    }
                });
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(jsonMessage));
            }
        }
    }

    public static void sendStaffStatusToClients(List<ListCurrentStaffStatusDto> currentStatusDtos) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonMessage = objectMapper.writeValueAsString(
                new java.util.HashMap<String, Object>() {
                    {
                        put("type", "staff");
                        put("data", currentStatusDtos);
                    }
                });
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(jsonMessage));
            }
        }
    }

}
