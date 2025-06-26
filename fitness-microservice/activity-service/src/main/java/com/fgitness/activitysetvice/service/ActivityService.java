package com.fgitness.activitysetvice.service;

import com.fgitness.activitysetvice.dto.ActivityRequest;
import com.fgitness.activitysetvice.dto.ActivityResponse;
import com.fgitness.activitysetvice.exception.UserNotFoundException;
import com.fgitness.activitysetvice.model.Activity;
import com.fgitness.activitysetvice.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final RabbitTemplate rabbitTemplate;
    private final UserValidationService userValidationService;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public ActivityResponse trackActivity(ActivityRequest request) {
        log.info("Track activity : {}", request);

        boolean isValidUser = userValidationService.validateUser(request.getUserId());

        if (!isValidUser) {
            throw new UserNotFoundException(request.getUserId());
        }

        Activity activity = Activity.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())
                .startTime(request.getStartTime())
                .additionalMetrics(request.getAdditionalMetrics())
                .build();

        Activity savedActivity = activityRepository.save(activity);
        log.info("Saved activity : {}", savedActivity);

        //Publish to RabbitMQ for AI processing
        try{
            rabbitTemplate.convertAndSend(exchange, routingKey, savedActivity);
        }catch (Exception e){
            log.error("Failed to publish to RabbitMq : ", e);
        }

        return mapToResponse(savedActivity);
    }
    private ActivityResponse mapToResponse(Activity activity){
        ActivityResponse activityResponse = new ActivityResponse();
        activityResponse.setId(activity.getId());
        activityResponse.setUserId(activity.getUserId());
        activityResponse.setType(activity.getType());
        activityResponse.setDuration(activity.getDuration());
        activityResponse.setCaloriesBurned(activity.getCaloriesBurned());
        activityResponse.setStartTime(activity.getStartTime());
        activityResponse.setAdditionalMetrics(activity.getAdditionalMetrics());
        activityResponse.setCreatedAt(activity.getCreatedAt());
        activityResponse.setUpdatedAt(activity.getUpdatedAt());

        return activityResponse;
    }

    public List<ActivityResponse> getAllActivities() {
        return activityRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<ActivityResponse> getUserActivities(String userId) {

        List<Activity> userActivities = activityRepository.findByUserId(userId);

        return userActivities.stream().map(this::mapToResponse).collect(Collectors.toList());

    }

    public ActivityResponse getActivity(String activityId) {
        return activityRepository.findById(activityId)
                .map(this::mapToResponse)
                .orElseThrow(()->new RuntimeException("Activity not found with id : " + activityId));
    }
}
