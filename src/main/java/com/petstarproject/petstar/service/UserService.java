package com.petstarproject.petstar.service;

import com.petstarproject.petstar.dto.UserCreateRequest;
import com.petstarproject.petstar.dto.UserResponse;

public interface UserService {

    UserResponse createUser(UserCreateRequest request);

    UserResponse getUserById(String userId);

    UserResponse getMe(String requesterId);
}
