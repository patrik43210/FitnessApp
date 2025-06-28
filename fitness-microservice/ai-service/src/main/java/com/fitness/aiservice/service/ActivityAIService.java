package com.fitness.aiservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.fitness.aiservice.helper.RecommendationHelper.createPromptForActivity;
import static com.fitness.aiservice.helper.RecommendationHelper.cleanJsonContent;
import static com.fitness.aiservice.helper.RecommendationHelper.buildRecommendation;
import static com.fitness.aiservice.helper.RecommendationHelper.createDefaultRecommendation;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {

    private static final String METHOD_REST = "METHOD_REST";
    private static final String METHOD_LIB = "METHOD_LIB";

    private final GeminiService geminiService;

    public Recommendation generateRecommendation(Activity activity, String method) {
        log.info("Generate Recommendation using method: {}",
                METHOD_REST.equals(method) ? "WebClient" : "Gemini Java Library");

        String prompt = createPromptForActivity(activity);
        String aiResponse;

        if (METHOD_REST.equals(method)) {
            aiResponse = geminiService.geminiAnswerRest(prompt);
            log.info("Response from AI completed");
           return processAiResponse(activity, aiResponse, method);
        } else if (METHOD_LIB.equals(method)) {
            aiResponse = geminiService.geminiAnswerLib(prompt);
            log.info("Response from AI completed");
           return processAiResponse(activity, aiResponse, method);
        } else {
            throw new IllegalArgumentException("Unknown method: " + method);
        }
    }

    private Recommendation processAiResponse(Activity activity, String aiResponse, String method) {
        try {
            String jsonContent;
            ObjectMapper mapper = new ObjectMapper();
            if (METHOD_REST.equals(method)) {
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

            log.info("AI response Processed");

            JsonNode analysisJson = mapper.readTree(jsonContent);
            JsonNode analysisNode = analysisJson.path("analysis");

            return buildRecommendation(activity, analysisJson, analysisNode);

        } catch (Exception e) {
            log.error("Error processing AI response", e);
            return createDefaultRecommendation(activity);
        }
    }
}

