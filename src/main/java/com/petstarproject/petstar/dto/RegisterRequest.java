package com.petstarproject.petstar.dto;

import com.petstarproject.petstar.enums.Gender;
import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private Integer age;
    private String species;
    private Gender gender;
    private String bio;
}
