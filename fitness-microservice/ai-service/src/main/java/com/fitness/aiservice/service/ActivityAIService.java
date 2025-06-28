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

import static com.fitness.aiservice.constant.AIServiceConstants.LOG_GENERATE_RECOMMENDATION;
import static com.fitness.aiservice.constant.AIServiceConstants.METHOD_REST;
import static com.fitness.aiservice.constant.AIServiceConstants.LOG_AI_RESPONSE_RECEIVED;
import static com.fitness.aiservice.constant.AIServiceConstants.ERROR_UNKNOWN_METHOD;
import static com.fitness.aiservice.constant.AIServiceConstants.LOG_AI_RESPONSE_PROCESSED;
import static com.fitness.aiservice.constant.AIServiceConstants.LOG_AI_RESPONSE_ERROR;
import static com.fitness.aiservice.constant.AIServiceConstants.METHOD_LIB;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {

    private final GeminiService geminiService;

    public Recommendation generateRecommendation(Activity activity, String method) {
        log.info(LOG_GENERATE_RECOMMENDATION, method.equals(METHOD_REST) ? "WebClient" : "Gemini Java Library");

        String prompt = createPromptForActivity(activity);
        String aiResponse;

        if (METHOD_REST.equals(method)) {
            aiResponse = geminiService.geminiAnswerRest(prompt);
            log.info(LOG_AI_RESPONSE_RECEIVED);
            return processAiResponse(activity, aiResponse, method);
        } else if (METHOD_LIB.equals(method)) {
            aiResponse = geminiService.geminiAnswerLib(prompt);
            log.info(LOG_AI_RESPONSE_RECEIVED);
            return processAiResponse(activity, aiResponse, method);
        } else {
            throw new IllegalArgumentException(ERROR_UNKNOWN_METHOD + method);
        }
    }

    private Recommendation processAiResponse(Activity activity, String aiResponse, String method) {
        try {
            String jsonContent;
            ObjectMapper mapper = new ObjectMapper();
            if (METHOD_REST.equals(method)) {
                JsonNode rootNode = mapper.readTree(aiResponse);
                JsonNode textNode = rootNode.path("candidates").get(0)
                        .path("content").path("parts").get(0).get("text");

                jsonContent = cleanJsonContent(textNode.asText());
            } else {
                jsonContent = cleanJsonContent(aiResponse);
            }

            log.info(LOG_AI_RESPONSE_PROCESSED);

            JsonNode analysisJson = mapper.readTree(jsonContent);
            JsonNode analysisNode = analysisJson.path("analysis");

            return buildRecommendation(activity, analysisJson, analysisNode);

        } catch (Exception e) {
            log.error(LOG_AI_RESPONSE_ERROR, e);
            return createDefaultRecommendation(activity);
        }
    }
}
