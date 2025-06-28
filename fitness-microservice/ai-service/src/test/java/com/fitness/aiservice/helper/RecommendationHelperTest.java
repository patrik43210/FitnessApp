package com.fitness.aiservice.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RecommendationHelperTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void testCleanJsonContent() {
        String dirty = "```json\n{\n  \"key\": \"value\"\n}\n```";
        String cleaned = RecommendationHelper.cleanJsonContent(dirty);

        assertEquals("{\n  \"key\": \"value\"\n}", cleaned);
    }

    @Test
    void testCreatePromptForActivity() {
        Activity activity = new Activity();
        activity.setType("Running");
        activity.setDuration(30);
        activity.setCaloriesBurned(250);
        activity.setAdditionalMetrics(Map.of("heartRate", "120 bpm"));

        String prompt = RecommendationHelper.createPromptForActivity(activity);

        assertTrue(prompt.contains("Running"));
        assertTrue(prompt.contains("30"));
        assertTrue(prompt.contains("250"));
        assertTrue(prompt.contains("heartRate"));
    }

    @Test
    void testBuildRecommendation() throws Exception {
        Activity activity = new Activity();
        activity.setId("activity123");
        activity.setUserId("user123");
        activity.setType("Cycling");

        String json = """
            {
              "analysis": {
                "overall": "Good session.",
                "pace": "Steady pace.",
                "heartRate": "Normal HR.",
                "caloriesBurned": "Burned enough."
              },
              "improvements": [
                { "area": "Endurance", "recommendation": "Increase duration." }
              ],
              "suggestions": [
                { "workout": "HIIT", "description": "Add intervals." }
              ],
              "safety": [
                "Stay hydrated."
              ]
            }
            """;

        JsonNode root = mapper.readTree(json);
        JsonNode analysisNode = root.path("analysis");

        Recommendation recommendation = RecommendationHelper.buildRecommendation(activity, root, analysisNode);

        assertEquals("activity123", recommendation.getActivityId());
        assertEquals("Cycling", recommendation.getActivityType());
        assertTrue(recommendation.getRecommendation().contains("Overall:Good session."));
        assertTrue(recommendation.getImprovements().get(0).contains("Endurance"));
        assertTrue(recommendation.getSuggestions().get(0).contains("HIIT"));
        assertTrue(recommendation.getSafety().contains("Stay hydrated."));
    }

    @Test
    void testCreateDefaultRecommendation() {
        Activity activity = new Activity();
        activity.setId("activity123");
        activity.setUserId("user123");
        activity.setType("Yoga");

        Recommendation recommendation = RecommendationHelper.createDefaultRecommendation(activity);

        assertEquals("activity123", recommendation.getActivityId());
        assertTrue(recommendation.getRecommendation().contains("No AI analysis"));
        assertFalse(recommendation.getImprovements().isEmpty());
        assertFalse(recommendation.getSuggestions().isEmpty());
        assertFalse(recommendation.getSafety().isEmpty());
    }
}
