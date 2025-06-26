package com.fgitness.activitysetvice.service;

import com.fgitness.activitysetvice.dto.ActivityRequest;
import com.fgitness.activitysetvice.dto.ActivityResponse;
import com.fgitness.activitysetvice.exception.UserNotFoundException;
import com.fgitness.activitysetvice.model.Activity;
import com.fgitness.activitysetvice.model.ActivityType;
import com.fgitness.activitysetvice.repository.ActivityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActivityServiceTest {

    private ActivityRepository activityRepository;
    private UserValidationService userValidationService;
    private ActivityService activityService;

    @BeforeEach
    void setup() {
        activityRepository = mock(ActivityRepository.class);
        userValidationService = mock(UserValidationService.class);
        activityService = new ActivityService(activityRepository, userValidationService);
    }

    @Test
    void trackActivity_shouldSaveAndReturnActivityResponse_whenUserIsValid() {
        // Given
        ActivityRequest request = new ActivityRequest();
        request.setUserId("user123");
        request.setType(ActivityType.RUNNING);
        request.setDuration(30);
        request.setCaloriesBurned(200);
        request.setStartTime(LocalDateTime.now());
        request.setAdditionalMetrics(Map.of("distance", 5.0));


        when(userValidationService.validateUser("user123")).thenReturn(true);

        Activity savedActivity = Activity.builder()
                .id("activity123")
                .userId("user123")
                .type(ActivityType.RUNNING)
                .duration(30)
                .caloriesBurned(200)
                .startTime(request.getStartTime())
                .additionalMetrics(request.getAdditionalMetrics())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(activityRepository.save(any())).thenReturn(savedActivity);

        // When
        ActivityResponse response = activityService.trackActivity(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo("user123");
        assertThat(response.getType()).isEqualTo(ActivityType.RUNNING);
        assertThat(response.getCaloriesBurned()).isEqualTo(200);
        verify(activityRepository, times(1)).save(any());
    }

    @Test
    void trackActivity_shouldThrowUserNotFoundException_whenUserIsInvalid() {
        // Given
        ActivityRequest request = new ActivityRequest();
        request.setUserId("invalid-user");

        when(userValidationService.validateUser("invalid-user")).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> activityService.trackActivity(request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("invalid-user");

        verify(activityRepository, never()).save(any());
    }

    @Test
    void getAllActivities_shouldReturnMappedList() {
        // Given
        List<Activity> mockActivities = List.of(
                Activity.builder().id("1").userId("u1").type(ActivityType.RUNNING).duration(20).build(),
                Activity.builder().id("2").userId("u2").type(ActivityType.CYCLING).duration(40).build()
        );

        when(activityRepository.findAll()).thenReturn(mockActivities);

        // When
        List<ActivityResponse> responses = activityService.getAllActivities();

        // Then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getUserId()).isEqualTo("u1");
        verify(activityRepository).findAll();
    }

    @Test
    void getUserActivities_shouldReturnFilteredList() {
        // Given
        List<Activity> mockUserActivities = List.of(
                Activity.builder().id("1").userId("u1").type(ActivityType.RUNNING).duration(25).build()
        );

        when(activityRepository.findByUserId("u1")).thenReturn(mockUserActivities);

        // When
        List<ActivityResponse> responses = activityService.getUserActivities("u1");

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getUserId()).isEqualTo("u1");
    }

    @Test
    void getActivity_shouldReturnActivityResponse_whenFound() {
        // Given
        Activity mockActivity = Activity.builder().id("act1").userId("u1").duration(30).build();

        when(activityRepository.findById("act1")).thenReturn(Optional.of(mockActivity));

        // When
        ActivityResponse response = activityService.getActivity("act1");

        // Then
        assertThat(response.getId()).isEqualTo("act1");
        assertThat(response.getUserId()).isEqualTo("u1");
    }

    @Test
    void getActivity_shouldThrowRuntimeException_whenNotFound() {
        // Given
        when(activityRepository.findById("unknown")).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> activityService.getActivity("unknown"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Activity not found with id : unknown");
    }
}
