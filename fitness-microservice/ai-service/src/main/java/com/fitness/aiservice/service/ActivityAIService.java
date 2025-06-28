package com.fitness.aiservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {

    private static final String METHOD_REST = "METHOD_REST";
    private static final String METHOD_LIB = "METHOD_LIB";

    private final GeminiService geminiService;

    public String generateRecommendation(Activity activity, String method) {
        log.info("Generate Recommendation using method: {}",
                METHOD_REST.equals(method) ? "WebClient" : "Gemini Java Library");

        String prompt = createPromptForActivity(activity);
        String aiResponse;

        if (METHOD_REST.equals(method)) {
            aiResponse = geminiService.geminiAnswerRest(prompt);
            processAiResponse(activity, aiResponse, method);
        } else if (METHOD_LIB.equals(method)) {
            aiResponse = geminiService.geminiAnswerLib(prompt);
            processAiResponse(activity, aiResponse, method);
        } else {
            throw new IllegalArgumentException("Unknown method: " + method);
        }

        log.info("Response from AI: {}", aiResponse);
        return aiResponse;
    }

    private void processAiResponse(Activity activity, String aiResponse, String method) {
        try {
            String jsonContent;
            if (METHOD_REST.equals(method)) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(aiResponse);
                JsonNode textNode = rootNode.path("candidates")
                        .get(0)
                        .path("content")
                        .path("parts")
                        .get(0)
                        .get("text");

                jsonContent = cleanJsonContent(textNode.asText());
            } else {
                jsonContent = cleanJsonContent(aiResponse);
            }

            log.info("Processed AI response: {}", jsonContent);

        } catch (Exception e) {
            log.error("Error processing AI response", e);
        }
    }

    private static String cleanJsonContent(String textNode) {
        return textNode
                .replaceAll("```json\\n", "")
                .replaceAll("\\n```", "")
                .trim();
    }

    private String createPromptForActivity(Activity activity) {
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
}

