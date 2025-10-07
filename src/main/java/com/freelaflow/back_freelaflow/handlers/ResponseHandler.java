package com.freelaflow.back_freelaflow.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ResponseHandler {
    public ResponseEntity<Map<String, Object>> generateResponse(String message, Object dados, HttpStatus status) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("message", message);
        body.put("dados", dados);

        return new ResponseEntity<>(body, status);
    }
}
