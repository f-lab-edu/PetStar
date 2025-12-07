package com.petstarproject.petstar.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstarproject.petstar.exception.JsonConvertException;
import org.springframework.stereotype.Component;

/**
 * JSON 문자열을 RegisterRequest 객체로 변환하는 기능을 담당하는 컴포넌트입니다.
 * 잘못된 JSON이 전달될 경우 JsonConvertException을 발생시킵니다.
 */
@Component
public class RegisterRequestJsonMapper {

    private final ObjectMapper objectMapper;

    public RegisterRequestJsonMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 주어진 JSON 문자열을 RegisterRequest 객체로 변환합니다.
     * @param json RegisterRequest 형태의 데이터를 담고 있는 JSON 문자열
     * @return 변환된 RegisterRequest 객체
     * @throws JsonConvertException JSON 파싱에 실패한 경우 발생
     */
    public RegisterRequest fromJson(String json) {
        try {
            return objectMapper.readValue(json, RegisterRequest.class);
        } catch (Exception e) {
            throw new JsonConvertException("Invalid JSON", e);
        }
    }
}
