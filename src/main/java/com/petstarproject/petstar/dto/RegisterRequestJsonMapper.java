package com.petstarproject.petstar.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstarproject.petstar.exception.JsonConverException;
import org.springframework.stereotype.Component;

@Component
public class RegisterRequestJsonMapper {

    private final ObjectMapper objectMapper;

    public RegisterRequestJsonMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public RegisterRequest fromJson(String json) {
        try {
            return objectMapper.readValue(json, RegisterRequest.class);
        } catch (Exception e) {
            throw new JsonConverException("Invalid JSON", e);
        }
    }
}
