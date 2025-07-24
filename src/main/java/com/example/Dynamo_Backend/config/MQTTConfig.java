package com.example.Dynamo_Backend.config;

import java.io.IOException;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
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

import com.example.Dynamo_Backend.service.OperateHistoryService;
import com.example.Dynamo_Backend.service.CurrentStatusService;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class MQTTConfig {
    final CurrentStatusService currentStatusService;
    final OperateHistoryService operateHistoryService;

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();

        options.setServerURIs(new String[] { "tcp://127.0.0.1:1883" });
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);
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
                    // currentStatusService.addCurrentStatus(message.getPayload().toString());
                    operateHistoryService.addOperateHistory(message.getPayload().toString());
                    // try {
                    // MyWebSocketHandler.sendMessageToClients(message.getPayload().toString());
                    // } catch (IOException e) {
                    // e.printStackTrace();
                    // }
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
