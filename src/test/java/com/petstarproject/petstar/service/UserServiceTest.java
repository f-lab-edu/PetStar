package com.petstarproject.petstar.service;

import com.petstarproject.petstar.dto.UserCreateRequest;
import com.petstarproject.petstar.dto.UserResponse;
import com.petstarproject.petstar.entity.User;
import com.petstarproject.petstar.exception.DuplicatedEmailException;
import com.petstarproject.petstar.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;


    @Test
    @DisplayName("이메일 중복 없으면 저장 후 UserResponse 반환")
    void createUser_success() {
        // given
        UserCreateRequest request = UserCreateRequest.builder()
                .email("test@petstar.com")
                .displayName("test_user")
                .build();

        when(userRepository.existsByEmail("test@petstar.com")).thenReturn(false);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        User savedUser = User.create("test@petstar.com", "test_user", null);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // when
        UserResponse response = userService.createUser(request);

        // then
        assertThat(response.getEmail()).isEqualTo("test@petstar.com");
        assertThat(response.getDisplayName()).isEqualTo("test_user");

        verify(userRepository).existsByEmail("test@petstar.com");
        verify(userRepository).save(userCaptor.capture());

        User passed = userCaptor.getValue();
        assertThat(passed.getEmail()).isEqualTo("test@petstar.com");
        assertThat(passed.getDisplayName()).isEqualTo("test_user");
    }

    @Test
    @DisplayName("이메일이 중복이면 DuplicatedEmailException발생")
    void createUser_duplicateEmail_precheck() {
        // given
        UserCreateRequest request = UserCreateRequest.builder()
                .email("dup@petstar.com")
                .displayName("dup")
                .build();

        when(userRepository.existsByEmail("dup@petstar.com")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(DuplicatedEmailException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Id가 존재하면 UserResponse 반환")
    void getUserById_success() {
        // given
        User user = User.create("test@petstar.com", "test_user", "bio");
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));

        // when
        UserResponse response = userService.getUserById("user-1");

        // then
        assertThat(response.getEmail()).isEqualTo("test@petstar.com");
        assertThat(response.getDisplayName()).isEqualTo("test_user");
    }

    @Test
    @DisplayName("Id가 없으면 UserNotFoundException(or EntityNotFoundException)")
    void getUserById_notFound() {
        // given
        when(userRepository.findById("missing")).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUserById("missing"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("getMe는 getUserById 호출")
    void getMe_delegates() {
        // given
        User user = User.create("me@petstar.com", "me", null);
        when(userRepository.findById("me-id")).thenReturn(Optional.of(user));

        // when
        UserResponse response = userService.getMe("me-id");

        // then
        assertThat(response.getEmail()).isEqualTo("me@petstar.com");
        verify(userRepository).findById("me-id");
    }
}