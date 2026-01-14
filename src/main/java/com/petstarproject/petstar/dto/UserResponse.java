package com.petstarproject.petstar.dto;

import com.petstarproject.petstar.entity.User;
import com.petstarproject.petstar.enums.UserRole;
import com.petstarproject.petstar.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private String id;
    private String email;
    private String displayName;
    private UserRole role;
    private UserStatus status;
    private String bio;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .role(user.getRole())
                .status(user.getStatus())
                .bio(user.getBio())
                .build();
    }
}
