package com.petstarproject.petstar.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    private String id;
    private String email;
    //    private String password;
    private String displayName;
    private String bio;
}
