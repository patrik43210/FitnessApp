package com.fitness.userservice.service;

import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.model.User;
import com.fitness.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        User user = getMockUser();
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));

        UserResponse response = userService.getUserProfile("user123");

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("user123");
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getLastName()).isEqualTo("Doe");
        assertThat(response.getPassword()).isEqualTo("pass123");

        verify(userRepository).findById("user123");
    }

    @Test
    void shouldThrowException_whenUserNotFound() {
        when(userRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserProfile("unknown"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void shouldRegisterNewUser_whenEmailIsUnique() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@example.com");
        request.setPassword("secure123");
        request.setFirstName("Alice");
        request.setLastName("Wonderland");

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);

        User userToSave = new User();
        userToSave.setEmail(request.getEmail());
        userToSave.setPassword(request.getPassword());
        userToSave.setFirstName(request.getFirstName());
        userToSave.setLastName(request.getLastName());

        User saved = getMockUser();
        when(userRepository.save(any(User.class))).thenReturn(saved);

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
        User u1 = getMockUser();
        User u2 = new User();
        u2.setId("u2");
        u2.setEmail("second@example.com");
        u2.setPassword("pwd");
        u2.setFirstName("Second");
        u2.setLastName("User");

        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        List<UserResponse> responses = userService.getAllUser();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getId()).isEqualTo("user123");
        assertThat(responses.get(1).getEmail()).isEqualTo("second@example.com");
    }

    @Test
    void shouldReturnTrueWhenUserIdExists() {
        when(userRepository.existsById("user123")).thenReturn(true);

        boolean exists = userService.existByUserId("user123");

        assertThat(exists).isTrue();
        verify(userRepository).existsById("user123");
    }

    @Test
    void shouldReturnFalseWhenUserIdDoesNotExist() {
        when(userRepository.existsById("user123")).thenReturn(false);

        boolean exists = userService.existByUserId("user123");

        assertThat(exists).isFalse();
        verify(userRepository).existsById("user123");
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
