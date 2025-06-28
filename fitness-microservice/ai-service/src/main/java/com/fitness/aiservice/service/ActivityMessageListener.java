package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {

    private final String METHOD_REST = "METHOD_REST";
    private final String METHOD_LIB = "METHOD_LIB";

    private final ActivityAIService aiService;
    private final RecommendationRepository recommendationRepository;

    @RabbitListener(queues = "activity.queue")
    public void processActivity(Activity activity){
        log.info("Received activity for processing: {}", activity.getId());
        Recommendation recommendation = aiService.generateRecommendation(activity, getRandomMethod());
        recommendationRepository.save(recommendation);
    }

    public String getRandomMethod() {
        return new Random().nextBoolean() ? METHOD_REST : METHOD_LIB;
    }
}
