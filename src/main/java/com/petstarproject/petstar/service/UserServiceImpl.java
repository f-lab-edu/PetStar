package com.petstarproject.petstar.service;

import com.petstarproject.petstar.dto.UserCreateRequest;
import com.petstarproject.petstar.dto.UserResponse;
import com.petstarproject.petstar.entity.User;
import com.petstarproject.petstar.exception.DuplicatedEmailException;
import com.petstarproject.petstar.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicatedEmailException(request.getEmail());
        }

        User user = User.create(request.getEmail(), request.getDisplayName(), null); // bio는 계정 생성후 설정
        User savedUser = userRepository.save(user);
        return UserResponse.from(savedUser);
    }


    @Transactional
    @Override
    public UserResponse getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(userId));
        return UserResponse.from(user);
    }


    @Transactional
    @Override
    public UserResponse getMe(String requesterId) {
        return getUserById(requesterId);
    } // todo: 추후 Security 기반으로 변경
}
