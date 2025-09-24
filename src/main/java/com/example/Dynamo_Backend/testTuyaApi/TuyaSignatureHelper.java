package com.example.Dynamo_Backend.testTuyaApi;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.apache.commons.codec.digest.HmacUtils;

import com.example.Dynamo_Backend.exception.BusinessException;

public class TuyaSignatureHelper {
    public static String generateSignature(String clientId, String secret, long timestamp, String nonce, String method,
            String body, String url) {
        String contentSha256 = sha256(body);
        String stringToSign = method.toUpperCase() + "\n" + contentSha256 + "\n" + "\n" + url;
        String signData = clientId + timestamp + nonce + stringToSign;

        return new HmacUtils("HmacSHA256", secret).hmacHex(signData).toUpperCase();
    }

    public static String generateSignatureWithAccessToken(String clientId, String accessToken, String secret,
            long timestamp, String nonce, String method,
            String body, String url) {

        String contentSha256 = sha256(body);
        String stringToSign = method.toUpperCase() + "\n" + contentSha256 + "\n" + "\n" + url;
        String signData = clientId + accessToken + timestamp + nonce + stringToSign;

        return new HmacUtils("HmacSHA256", secret).hmacHex(signData).toUpperCase();
    }

    private static String sha256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new BusinessException("Error generating SHA-256 hash");
        }
    }

    public static String generateNonce() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder nonce = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            int randomIndex = (int) (Math.random() * chars.length());
            nonce.append(chars.charAt(randomIndex));
        }
        return nonce.toString();
    }
}
