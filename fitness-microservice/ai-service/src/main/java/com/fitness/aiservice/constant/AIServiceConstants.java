package com.fitness.aiservice.constant;

public class AIServiceConstants {

    private AIServiceConstants() {
        // Utility class
    }

    // Log messages
    public static final String LOG_GENERATE_RECOMMENDATION = "Generate Recommendation using method: {}";
    public static final String LOG_AI_RESPONSE_RECEIVED = "Response from AI completed";
    public static final String LOG_AI_RESPONSE_PROCESSED = "AI response Processed";
    public static final String LOG_AI_RESPONSE_ERROR = "Error processing AI response";
    public static final String LOG_RECEIVED_ACTIVITY = "Received activity for processing: {}";

    // Error messages
    public static final String ERROR_UNKNOWN_METHOD = "Unknown method: ";
    public static final String ERROR_NO_RECOMMENDATION_FOUND = "No Recommendation Found for this activity: ";

    // Misc
    public static final String METHOD_REST = "METHOD_REST";
    public static final String METHOD_LIB = "METHOD_LIB";
    public static final String GEMINI_MODEL = "gemini-2.5-flash";
}
