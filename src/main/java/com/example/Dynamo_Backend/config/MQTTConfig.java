package com.example.Dynamo_Backend.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import com.example.Dynamo_Backend.dto.ResponseDto.CurrentStatusResponseDto;
import com.example.Dynamo_Backend.dto.ResponseDto.GroupResponseDto;
import com.example.Dynamo_Backend.dto.ResponseDto.ListCurrentStaffStatusDto;
import com.example.Dynamo_Backend.event.OperateHistoryMessageEvent;
import com.example.Dynamo_Backend.service.CurrentStatusService;
import com.example.Dynamo_Backend.service.GroupService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class MQTTConfig {
    private final CurrentStatusService currentStatusService;
    private final ApplicationEventPublisher eventPublisher;
    private final GroupService groupService;

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();

        // options.setServerURIs(new String[] { "tcp://10.60.253.11:1883" });
        options.setServerURIs(new String[] { "tcp://172.21.200.20:1883" });
        // options.setServerURIs(new String[] {
        // "ssl://7b4d336ebd84424cb04e1c2900fe72d0.s1.eu.hivemq.cloud:8883" });
        // 172.21.200.20
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);
        options.setUserName("iic_mqtt");
        options.setPassword("Vsiic_2024".toCharArray());
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("serverIn",
                mqttClientFactory(), "myTopic");

        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(2);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {

        return new MessageHandler() {

            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC).toString();
                if (topic.equals("myTopic")) {
                    System.out.println(message.getPayload().toString());
                    currentStatusService.addCurrentStatus(message.getPayload().toString());
                    eventPublisher.publishEvent(new OperateHistoryMessageEvent(message.getPayload().toString()));
                    GroupResponseDto groupDto = groupService.getGroupByMachineId(message.getPayload().toString());
                    if (groupDto != null) {
                        List<CurrentStatusResponseDto> statusList = currentStatusService
                                .getCurrentStatusByGroupId(groupDto.getGroupId());

                        GroupResponseDto groupDto1 = groupService.getGroupByStaffId(message.getPayload().toString());
                        List<ListCurrentStaffStatusDto> listStaffStatus = new ArrayList<>();
                        if (groupDto1 != null) {
                            listStaffStatus = currentStatusService
                                    .getCurrentStaffStatusByGroupId(groupDto1.getGroupId());
                        }

                        Map<String, Long> statusCount = groupService.getGroupCountByGroupId(groupDto.getGroupId());
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            String jsonMessage = objectMapper.writeValueAsString(
                                    new java.util.HashMap<String, Object>() {
                                        {
                                            put("type", groupDto.getGroupName().concat("-status"));
                                            put("data", statusList);
                                            put("countStatus", statusCount);
                                        }
                                    });
                            MyWebSocketHandler.sendGroupStatusToClients(jsonMessage);
                            MyWebSocketHandler.sendStaffStatusToClients(listStaffStatus);

                            // String statusCountJson = objectMapper.writeValueAsString(
                            // Map.of("type", groupDto.getGroupName().concat("-countStatus"), "data",
                            // statusCount));
                            // MyWebSocketHandler.sendGroupStatusToClients(statusCountJson);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        MyWebSocketHandler.sendMessageToClients(message.getPayload().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // String[] arr = message.getPayload().toString().split(",");

                // ObjectMapper objectMapper = new ObjectMapper();
                // String json;
                // try {
                // json = objectMapper.writeValueAsString(users);
                // // Send update via WebSocket
                // MyWebSocketHandler.sendMessageToClients(json);
                // } catch (JsonProcessingException e) {
                // e.printStackTrace();
                // } catch (IOException e) {
                // e.printStackTrace();
                // }

            }

        };
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        // clientId is generated using a random number
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("serverOut", mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic("myTopic");
        messageHandler.setDefaultRetained(false);
        return messageHandler;
    }
}
