package com.example.Dynamo_Backend.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Dynamo_Backend.dto.RequestDto.PasswordResetEmail;
import com.example.Dynamo_Backend.dto.RequestDto.ResetPasswordRequest;
import com.example.Dynamo_Backend.exception.*;
import com.example.Dynamo_Backend.service.implementation.PasswordResetServiceImplementation;

@RestController
@RequestMapping("/api/password-reset")
public class PasswordResetController {

    @Autowired
    private PasswordResetServiceImplementation passwordResetService;

    @PostMapping("/initiate")
    public ResponseEntity<Map<String, String>> initiatePasswordReset(
            @RequestBody PasswordResetEmail request) {

        try {
            passwordResetService.initiatePasswordReset(request.getEmail());

            Map<String, String> response = new HashMap<>();
            response.put("message", "sent");
            return ResponseEntity.ok(response);

        } catch (ResourceNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestBody ResetPasswordRequest request) {
        try {
            passwordResetService.resetPassword(request.getToken(), request.getNewPassword(), request.getMethod(),
                    request.getConfirmPassword(), request.getCurrentPassword());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Password reset successfully");
            return ResponseEntity.ok(response);

        } catch (InvalidTokenException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/validate/{token}")
    public ResponseEntity<Map<String, Boolean>> validateToken(@PathVariable String token) {
        boolean isValid = passwordResetService.validateResetToken(token);

        Map<String, Boolean> response = new HashMap<>();
        response.put("valid", isValid);
        return ResponseEntity.ok(response);
    }
}
