package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ActivityAIServiceTest {

    private final GeminiService geminiService = mock(GeminiService.class);
    private final ActivityAIService aiService = new ActivityAIService(geminiService);

    @Test
    void testGenerateRecommendationRest() {
        Activity activity = new Activity();
        when(geminiService.geminiAnswerRest(anyString())).thenReturn("{ \"candidates\": [ { \"content\": { \"parts\": [ { \"text\": \"{\\\"analysis\\\":{\\\"overall\\\":\\\"good\\\"}\" } ] } } ] }");

        aiService.generateRecommendation(activity, "METHOD_REST");
        verify(geminiService).geminiAnswerRest(anyString());
    }

    @Test
    void testGenerateRecommendationLib() {
        Activity activity = new Activity();
        when(geminiService.geminiAnswerLib(anyString())).thenReturn("{ \"analysis\": { \"overall\": \"good\" } }");

        aiService.generateRecommendation(activity, "METHOD_LIB");
        verify(geminiService).geminiAnswerLib(anyString());
    }

    @Test
    void testGenerateRecommendationInvalidMethod() {
        Activity activity = new Activity();
        assertThrows(IllegalArgumentException.class, () -> aiService.generateRecommendation(activity, "INVALID"));
    }
}
