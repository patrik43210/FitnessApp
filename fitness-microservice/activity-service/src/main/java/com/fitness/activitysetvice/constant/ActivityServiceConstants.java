package com.fitness.activitysetvice.constant;

public class ActivityServiceConstants {

    private ActivityServiceConstants() {
        // Utility class
    }

    public static final String LOG_TRACK_ACTIVITY = "Track activity : {}";
    public static final String LOG_SAVED_ACTIVITY = "Saved activity : {}";
    public static final String LOG_RABBITMQ_FAIL = "Failed to publish to RabbitMq : ";
    public static final String LOG_USER_VALIDATION = "Calling User Validation API for userId: {}";

    public static final String ACTIVITY_NOT_FOUND = "Activity not found with id : ";
    public static final String USER_NOT_FOUND = "User not Found :";
    public static final String INVALID_USER_REQUEST = "Invalid request :";
    public static final String UNKNOWN_ERROR = "Something went wrong";

}
