package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecommendationServiceTest {

    private final RecommendationRepository recommendationRepository = mock(RecommendationRepository.class);
    private final RecommendationService recommendationService = new RecommendationService(recommendationRepository);

    @Test
    void testGetUserRecommendation() {
        Recommendation rec = new Recommendation();
        when(recommendationRepository.findByUserId("user123")).thenReturn(List.of(rec));

        List<Recommendation> result = recommendationService.getUserRecommendation("user123");

        assertEquals(1, result.size());
        verify(recommendationRepository).findByUserId("user123");
    }

    @Test
    void testGetActivityRecommendation_found() {
        Recommendation rec = new Recommendation();
        when(recommendationRepository.findByActivityId("activity123")).thenReturn(Optional.of(rec));

        Recommendation result = recommendationService.getActivityRecommendation("activity123");

        assertNotNull(result);
        verify(recommendationRepository).findByActivityId("activity123");
    }

    @Test
    void testGetActivityRecommendation_notFound() {
        when(recommendationRepository.findByActivityId("activity123")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> recommendationService.getActivityRecommendation("activity123"));

        assertTrue(ex.getMessage().contains("No Recommendation Found"));
    }
}
