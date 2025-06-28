package com.fitness.aiservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {

    private final String METHOD_REST = "METHOD_REST";
    private final String METHOD_LIB = "METHOD_LIB";
    private final GeminiService geminiService;

    public String generateRecommendation(Activity activity, String method){


        log.info("Generate Recommendation using this method: {}",
                method.equals(METHOD_REST) ? "WebClient" : "Maven Gemini library");

        String prompt = createPromptForActivity(activity);
        String aiResponse;


        if(method.equals(METHOD_REST)){
            aiResponse = geminiService.geminiAnswerRest(prompt);
            processAiRestResponse(activity, aiResponse, method);
            //return the gemini json and the recommendation
        } else if (method.equals(METHOD_LIB)) {
            aiResponse = geminiService.geminiAnswerLib(prompt);
            processAiRestResponse(activity, aiResponse, method);
            //return the recommendation json
        }else {
            throw new RuntimeException("Please choose method of calling the api : " + method);
        }

        log.info("RESPONSE FROM AI: {}", aiResponse);
        return aiResponse;
    }

    private void processAiRestResponse(Activity activity, String aiResponse, String method){
        String jsonContent;
        try {
            if(method.equals(METHOD_REST)) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(aiResponse);
                JsonNode textNode = rootNode.path("candidates")
                        .get(0)
                        .path("content")
                        .path("parts")
                        .get(0)
                        .get("text");

                jsonContent = getJsonContent(textNode.asText());
            }else {
                jsonContent = getJsonContent(aiResponse);
            }

            log.info("PASSED RESPONSE FROM AI: {}", jsonContent);

        }catch (Exception e){
            e.printStackTrace();

        }
    }

    private static String getJsonContent(String textNode) {
        return textNode
                .replaceAll("```json\\n","")
                .replaceAll("\\n```","").trim();
    }

    private String createPromptForActivity(Activity activity) {
        return String.format(
                """
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
                activity.getAdditionalMetrics()
        );
    }
}
