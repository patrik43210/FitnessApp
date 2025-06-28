package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Random;

import static com.fitness.aiservice.constant.AIServiceConstants.LOG_RECEIVED_ACTIVITY;
import static com.fitness.aiservice.constant.AIServiceConstants.METHOD_REST;
import static com.fitness.aiservice.constant.AIServiceConstants.METHOD_LIB;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {

    private final ActivityAIService aiService;
    private final RecommendationRepository recommendationRepository;

    @RabbitListener(queues = "activity.queue")
    public void processActivity(Activity activity) {
        log.info(LOG_RECEIVED_ACTIVITY, activity.getId());
        Recommendation recommendation = aiService.generateRecommendation(activity, getRandomMethod());
        recommendationRepository.save(recommendation);
    }

    public String getRandomMethod() {
        return new Random().nextBoolean() ? METHOD_REST : METHOD_LIB;
    }
}
