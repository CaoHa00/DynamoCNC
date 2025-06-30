package com.example.Dynamo_Backend.testTuyaApi;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.AllArgsConstructor;

import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class TuyaAPIService {
    private static final String CLIENT_ID = "vmc8hpskpcx35ea5qgu8";
    private static final String SECRET = "5c3f6a9efb0e484e9a5eae2d0462dd4f";
    private static final String BASE_URL = "https://openapi.tuyaus.com";

    private String accessToken = null;
    private long tokenExpiry = 0;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getAccessToken() {
        long currentTime = System.currentTimeMillis();
        if (accessToken == null || currentTime >= tokenExpiry) {
            fetchAccessToken();
        }
        return accessToken;
    }

    private void fetchAccessToken() {

        long timestamp = System.currentTimeMillis();
        String nonce = TuyaSignatureHelper.generateNonce();
        String method = "GET";
        String body = ""; // Empty for GET requests
        String url = "/v1.0/token?grant_type=1"; // URL path

        String sign = TuyaSignatureHelper.generateSignature(CLIENT_ID, SECRET, timestamp, nonce, method, body, url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("client_id", CLIENT_ID);
        headers.set("sign", sign);
        headers.set("t", String.valueOf(timestamp));
        headers.set("nonce", nonce);
        headers.set("sign_method", "HMAC-SHA256");

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(BASE_URL + url, HttpMethod.GET, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> bodyMap = response.getBody();
            if (bodyMap.containsKey("result")) {
                Map<String, Object> result = (Map<String, Object>) bodyMap.get("result");
                accessToken = (String) result.get("access_token");
                tokenExpiry = System.currentTimeMillis() + ((Integer) result.get("expire_time") * 1000);
                System.out.println("Access Token: " + accessToken);
            } else {
                System.out.println("Error: " + bodyMap.get("msg"));
            }
        } else {
            System.out.println("Failed to fetch token. HTTP Status: " + response.getStatusCode());
        }
    }

    public String getListDevicesProperty() {
        String[] devices = { "6cf85b50df5c642854bo2n" };
        String method = "GET";
        String body = "";
        String url = " /v1.0/iot-03/devices/properties?device_ids" + devices + "&code=installLocation"; // API Path
        ResponseEntity<String> response = getResponse(url, method, body);
        String responseBody = response.getBody();
        return responseBody;
    }

    public String getDeviceStatus() {
        String devices = "eba57e20841de179d5aikw";
        String method = "GET";
        String body = "";
        String url = "/v1.0/iot-03/devices/" + devices + "/status"; // API Path
        ResponseEntity<String> response = getResponse(url, method, body);
        String responseBody = response.getBody();
        return responseBody;
    }

    public String getListDevicesPropertyByDate() {
        String deviceId = "eba57e20841de179d5aikw";
        String method = "GET";
        String body = "";
        String url = "/v1.0/iot-03/devices/"
                + deviceId + "/functions";
        ResponseEntity<String> response = getResponse(url, method, body);
        String responseBody = response.getBody();
        return responseBody;
    }

    public String getDeviceProperty(String deviceId) {
        String method = "GET";
        String body = "";
        String url = "/v2.0/cloud/thing/" + deviceId + "/shadow/properties"; // API Path

        ResponseEntity<String> response = getResponse(url, method, body);
        String responseBody = response.getBody();

        if (response.getStatusCode() == HttpStatus.OK && responseBody != null) {
            return extractPropertiesFromResponse(responseBody);
        }
        return responseBody;
    }

    private ResponseEntity<String> getResponse(String url, String method, String body) {
        getAccessToken(); // Ensure access token is valid
        long timestamp = System.currentTimeMillis();
        String nonce = TuyaSignatureHelper.generateNonce();

        // 🔥 Include accessToken in the signature
        String sign = TuyaSignatureHelper.generateSignatureWithAccessToken(CLIENT_ID, accessToken, SECRET, timestamp,
                nonce, method, body, url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("client_id", CLIENT_ID);
        headers.set("access_token", accessToken);
        headers.set("sign", sign);
        headers.set("t", String.valueOf(timestamp));
        headers.set("nonce", nonce);
        headers.set("sign_method", "HMAC-SHA256");

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(BASE_URL + url, HttpMethod.GET, request, String.class);
        return response;
    }

    private String extractPropertiesFromResponse(String responseBody) {

        try {

            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONObject result = jsonResponse.getJSONObject("result");
            JSONArray properties = result.getJSONArray("properties");

            // Extract "phase_a" (which contains raw data for voltage, current, and power)
            for (int i = 0; i < properties.length(); i++) {
                JSONObject property = properties.getJSONObject(i);
                String code = property.getString("code");
                Object value = property.get("value");

                // Check for the specific codes we're interested in
                if ("phase_a".equals(code)) {
                    if (value instanceof String) {
                        return parsePhaseA((String) value); // Parse and return the extracted values
                    } else {
                        return "Invalid phase_a value format.";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing response: " + e.getMessage();
        }
        return "No phase_a data found";
    }

    private String parsePhaseA(String base64Value) {
        try {
            // Decode the Base64 encoded value
            byte[] decodedBytes = Base64.getDecoder().decode(base64Value);

            // Assuming the decoded data format: [voltage (2 bytes), current (3 bytes),
            // power (3 bytes)]
            // Example: A phase_a string like "CPUAO7oACNo=" is decoded into byte array

            // Extract voltage, current, and power
            int voltage = (decodedBytes[0] << 8) | (decodedBytes[1] & 0xFF); // 2 bytes for voltage
            int current = (decodedBytes[2] << 16) | (decodedBytes[3] << 8) | (decodedBytes[4] & 0xFF); // 3 bytes for
                                                                                                       // current
            int power = (decodedBytes[5] << 16) | (decodedBytes[6] << 8) | (decodedBytes[7] & 0xFF); // 3 bytes for
                                                                                                     // power

            // Convert current and power to the required units
            double voltageInV = voltage * 0.1; // Voltage in 0.1V units
            double currentInA = current * 0.001; // Current in 0.001A units
            double powerInKW = power * 0.001; // Power in 0.001kW units

            return String.format("Voltage: %.1fV, Current: %.3fA, Power: %.3fkW", voltageInV, currentInA, powerInKW);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error parsing phase_a data: " + e.getMessage();
        }
    }

}
