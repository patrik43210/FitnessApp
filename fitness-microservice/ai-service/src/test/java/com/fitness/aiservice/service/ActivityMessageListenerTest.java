package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.*;

class ActivityMessageListenerTest {

    private final ActivityAIService aiService = mock(ActivityAIService.class);
    private final RecommendationRepository recommendationRepository = mock(RecommendationRepository.class);
    private final ActivityMessageListener listener = new ActivityMessageListener(aiService, recommendationRepository);

    @Test
    void testProcessActivity() {
        Activity activity = new Activity();
        Recommendation recommendation = new Recommendation();

        when(aiService.generateRecommendation(any(), any())).thenReturn(recommendation);

        listener.processActivity(activity);

        verify(aiService).generateRecommendation(eq(activity), anyString());
        verify(recommendationRepository).save(recommendation);
    }

    @Test
    void testGetRandomMethod() {
        String method = listener.getRandomMethod();
        assertTrue(method.equals("METHOD_REST") || method.equals("METHOD_LIB"));
    }
}
