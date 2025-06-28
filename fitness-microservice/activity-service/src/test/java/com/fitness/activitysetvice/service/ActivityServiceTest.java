package com.fitness.activitysetvice.service;

import com.fitness.activitysetvice.dto.ActivityRequest;
import com.fitness.activitysetvice.dto.ActivityResponse;
import com.fitness.activitysetvice.exception.UserNotFoundException;
import com.fitness.activitysetvice.model.Activity;
import com.fitness.activitysetvice.model.ActivityType;
import com.fitness.activitysetvice.repository.ActivityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActivityServiceTest {

    private ActivityRepository activityRepository;
    private UserValidationService userValidationService;
    private RabbitTemplate rabbitTemplate;
    private ActivityService activityService;

    @BeforeEach
    void setup() {
        activityRepository = mock(ActivityRepository.class);
        userValidationService = mock(UserValidationService.class);
        rabbitTemplate = mock(RabbitTemplate.class);

        activityService = new ActivityService(activityRepository, rabbitTemplate, userValidationService);

        // Inject dummy RabbitMQ config values
        org.springframework.test.util.ReflectionTestUtils.setField(activityService, "exchange", "test.exchange");
        org.springframework.test.util.ReflectionTestUtils.setField(activityService, "routingKey", "test.routingKey");
    }

    @Test
    void trackActivity_shouldSaveAndPublish_whenUserIsValid() {
        ActivityRequest request = new ActivityRequest();
        request.setUserId("user123");
        request.setType(ActivityType.RUNNING);
        request.setDuration(30);
        request.setCaloriesBurned(300);
        request.setStartTime(LocalDateTime.now());
        request.setAdditionalMetrics(Map.of("distance", 5.5));

        when(userValidationService.validateUser("user123")).thenReturn(true);

        Activity saved = Activity.builder()
                .id("id1")
                .userId("user123")
                .type(ActivityType.RUNNING)
                .duration(30)
                .caloriesBurned(300)
                .startTime(request.getStartTime())
                .additionalMetrics(request.getAdditionalMetrics())
                .build();

        when(activityRepository.save(any())).thenReturn(saved);

        ActivityResponse response = activityService.trackActivity(request);

        assertThat(response.getUserId()).isEqualTo("user123");
        verify(activityRepository).save(any());
        verify(rabbitTemplate).convertAndSend("test.exchange", "test.routingKey", saved);
    }

    @Test
    void trackActivity_shouldLogErrorWhenRabbitMQFails() {
        ActivityRequest request = new ActivityRequest();
        request.setUserId("user123");
        request.setType(ActivityType.CYCLING);

        when(userValidationService.validateUser("user123")).thenReturn(true);
        when(activityRepository.save(any())).thenReturn(Activity.builder().userId("user123").build());

        doThrow(new RuntimeException("RabbitMQ down"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), Optional.ofNullable(any()));

        ActivityResponse response = activityService.trackActivity(request);

        assertThat(response.getUserId()).isEqualTo("user123");
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), Optional.ofNullable(any()));
    }

    @Test
    void trackActivity_shouldThrowUserNotFoundException() {
        ActivityRequest request = new ActivityRequest();
        request.setUserId("invalid");

        when(userValidationService.validateUser("invalid")).thenReturn(false);

        assertThatThrownBy(() -> activityService.trackActivity(request))
                .isInstanceOf(UserNotFoundException.class);
        verifyNoInteractions(rabbitTemplate);
    }

    @Test
    void getAllActivities_shouldReturnMapped() {
        when(activityRepository.findAll()).thenReturn(List.of(
                Activity.builder().id("1").userId("u1").type(ActivityType.RUNNING).duration(20).build(),
                Activity.builder().id("2").userId("u2").type(ActivityType.CYCLING).duration(40).build()
        ));

        List<ActivityResponse> responses = activityService.getAllActivities();

        assertThat(responses).hasSize(2);
    }

    @Test
    void getUserActivities_shouldReturnCorrectList() {
        when(activityRepository.findByUserId("u1"))
                .thenReturn(List.of(Activity.builder().id("1").userId("u1").build()));

        List<ActivityResponse> responses = activityService.getUserActivities("u1");

        assertThat(responses).hasSize(1);
    }

    @Test
    void getActivity_shouldReturnResponse() {
        when(activityRepository.findById("a1"))
                .thenReturn(Optional.of(Activity.builder().id("a1").userId("u1").build()));

        ActivityResponse resp = activityService.getActivity("a1");

        assertThat(resp.getId()).isEqualTo("a1");
    }

    @Test
    void getActivity_shouldThrow_whenNotFound() {
        when(activityRepository.findById("x")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> activityService.getActivity("x"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Activity not found");
    }
}
