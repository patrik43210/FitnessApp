package com.fitness.userservice.service;

import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.model.User;
import com.fitness.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void shouldReturnUserProfile_whenUserExists() {
        User mockUser = getMockUser();
        when(userRepository.findById("user123")).thenReturn(Optional.of(mockUser));

        UserResponse response = userService.getUserProfile("user123");

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        verify(userRepository).findById("user123");
    }

    @Test
    void shouldThrowException_whenUserNotFound() {
        when(userRepository.findById("user123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserProfile("user123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void shouldRegisterNewUser_whenEmailIsUnique() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("john@example.com");
        request.setPassword("pass123");
        request.setFirstName("John");
        request.setLastName("Doe");

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);

        User savedUser = getMockUser();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.register(request);

        assertThat(response.getEmail()).isEqualTo("john@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowException_whenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("john@example.com");
        request.setPassword("pass123");
        request.setFirstName("John");
        request.setLastName("Doe");

        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email already exist");

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldReturnAllUsers() {
        User user1 = getMockUser();
        User user2 = new User();
        user2.setId("2");
        user2.setEmail("jane@example.com");
        user2.setPassword("pass456");
        user2.setFirstName("Jane");
        user2.setLastName("Smith");

        List<User> mockUsers = List.of(user1, user2);

        when(userRepository.findAll()).thenReturn(mockUsers);

        List<UserResponse> responses = userService.getAllUser();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(1).getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void shouldReturnTrueIfUserExistsById() {
        when(userRepository.existsById("user123")).thenReturn(true);

        assertThat(userService.existByUserId("user123")).isTrue();
    }

    @Test
    void shouldReturnFalseIfUserDoesNotExistById() {
        when(userRepository.existsById("user123")).thenReturn(false);

        assertThat(userService.existByUserId("user123")).isFalse();
    }

    private User getMockUser() {
        User user = new User();
        user.setId("user123");
        user.setEmail("john@example.com");
        user.setPassword("pass123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }
}
