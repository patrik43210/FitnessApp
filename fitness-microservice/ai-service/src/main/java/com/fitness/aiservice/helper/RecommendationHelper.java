package com.fitness.aiservice.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecommendationHelper {


    private static void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        if(!analysisNode.path(key).isMissingNode()) {
            fullAnalysis.append(prefix).append(analysisNode.path(key).asText()).append("\n\n");
        }
    }

    private static List<String> extractJsonLists(JsonNode jsonNode, String key, String prefix) {
        List<String> jsonList = new ArrayList<>();
        if(jsonNode.isArray()){
            jsonNode.forEach(jsonItem -> {
                String jsonKey  = jsonItem.path(key).asText();
                String jsonPrefix = jsonItem.path(prefix).asText();
                jsonList.add(String.format("%s: %s",jsonKey,jsonPrefix));
            });
        }
        return jsonList.isEmpty() ?
                Collections.singletonList(String.format("No specific %s provided",key)) :
                jsonList;
    }

    private static List<String> extractSafetyJson(JsonNode safetyNode) {
        List<String> jsonList = new ArrayList<>();
        if(safetyNode.isArray()){
            safetyNode.forEach(jsonItem -> {
                jsonList.add(jsonItem.asText());
            });
        }
        return jsonList.isEmpty() ?
                Collections.singletonList("Follow general safety guidelines") :
                jsonList;
    }

    public static String cleanJsonContent(String textNode) {
        return textNode
                .replaceAll("```json\\n", "")
                .replaceAll("\\n```", "")
                .trim();
    }

    public static String createPromptForActivity(Activity activity) {
        return String.format("""
                Analyse this fitness activity and provide a detailed recommendation in the following EXACT JSON format:

                {
                  "analysis": {
                    "overall": "Overall analysis here",
                    "pace": "Pace analysis here",
                    "heartRate": "Heart rate analysis here",
                    "caloriesBurned": "Calories analysis here"
                  },
                  "improvements": [
                    {
                      "area": "Area name",
                      "recommendation": "Detailed recommendation"
                    }
                  ],
                  "suggestions": [
                    {
                      "workout": "Workout name",
                      "description": "Detailed workout description"
                    }
                  ],
                  "safety": [
                    "Safety point 1",
                    "Safety point 2"
                  ]
                }

                Analyze this activity:
                - Activity Type: %s
                - Duration: %d minutes
                - Calories Burned: %d
                - Additional Metrics: %s

                Provide a detailed analysis focusing on performance, improvements, next workout suggestions, and safety guidelines.
                Ensure the response strictly follows the EXACT JSON format shown above.
                """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics());
    }

    public static Recommendation buildRecommendation(Activity activity, JsonNode analysisJson, JsonNode analysisNode) {
        StringBuilder fullAnalysis = new StringBuilder();
        addAnalysisSection(fullAnalysis, analysisNode, "overall", "Overall:");
        addAnalysisSection(fullAnalysis, analysisNode, "pace", "Pace:");
        addAnalysisSection(fullAnalysis, analysisNode, "heartRate", "Heart Rate:");
        addAnalysisSection(fullAnalysis, analysisNode, "caloriesBurned", "Calories Burned:");

        List<String> improvements = extractJsonLists(analysisJson.path("improvements"), "area", "recommendation");
        List<String> suggestions = extractJsonLists(analysisJson.path("suggestions"), "workout", "description");
        List<String> safety = extractSafetyJson(analysisJson.path("safety"));


        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getType())
                .recommendation(fullAnalysis.toString().trim())
                .improvements(improvements)
                .suggestions(suggestions)
                .safety(safety)
                .createAt(LocalDateTime.now())
                .build();
    }

    public static Recommendation createDefaultRecommendation(Activity activity) {
        List<String> defaultImprovements = List.of("Consider consulting with a trainer for personalized improvements.");
        List<String> defaultSuggestions = List.of("Try adding varied workouts to your routine for better results.");
        List<String> defaultSafety = List.of("Always warm up and cool down properly.", "Stay hydrated during workouts.");

        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getType())
                .recommendation("No AI analysis was available. Here are some general recommendations for your activity.")
                .improvements(defaultImprovements)
                .suggestions(defaultSuggestions)
                .safety(defaultSafety)
                .createAt(LocalDateTime.now())
                .build();
    }
}
