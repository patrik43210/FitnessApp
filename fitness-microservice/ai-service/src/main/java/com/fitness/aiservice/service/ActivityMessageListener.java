package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {

    private final ActivityAIService aiService;

    @RabbitListener(queues = "activity.queue")
    public void processActivity(Activity activity){
        log.info("Received activity for processing: {}", activity.getId());
        log.info("Generated Recommendation: {}", aiService.generateRecommendation(activity, getRandomMethod()));
    }

    public String getRandomMethod() {
        String METHOD_REST = "METHOD_REST";
        String METHOD_LIB = "METHOD_LIB";
        return new Random().nextBoolean() ? METHOD_REST : METHOD_LIB;
    }
}
