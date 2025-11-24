package com.petstarproject.petstar.dto;

import com.petstarproject.petstar.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PetInfoResponse {
    private String id;
    private String profileImageKey;
    private String name;
    private Integer age;
    private String species;
    private Gender gender;
    private String bio; // 소개글
}
