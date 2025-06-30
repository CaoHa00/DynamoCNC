package com.example.Dynamo_Backend.testTuyaApi;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tuya")
public class TuyaController {

    private final TuyaAPIService tuyaApiService;

    public TuyaController(TuyaAPIService tuyaApiService) {
        this.tuyaApiService = tuyaApiService;
    }

    @GetMapping("/device/{deviceId}")
    public String getDeviceStatus(@PathVariable String deviceId) {
        return tuyaApiService.getDeviceProperty(deviceId);
    }

    @GetMapping("/devices")
    public String getDevicesProperty() {
        return tuyaApiService.getDeviceStatus();
    }
}
