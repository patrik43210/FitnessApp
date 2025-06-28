package com.fitness.activitysetvice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static com.fitness.activitysetvice.constant.ActivityServiceConstants.LOG_USER_VALIDATION;
import static com.fitness.activitysetvice.constant.ActivityServiceConstants.USER_NOT_FOUND;
import static com.fitness.activitysetvice.constant.ActivityServiceConstants.INVALID_USER_REQUEST;
import static com.fitness.activitysetvice.constant.ActivityServiceConstants.UNKNOWN_ERROR;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidationService {

    private final WebClient userServiceWebClient;

    public boolean validateUser(String userId) {
        log.info(LOG_USER_VALIDATION, userId);
        try {
            return Boolean.TRUE.equals(userServiceWebClient
                    .get()
                    .uri("/api/users/{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block());
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException(USER_NOT_FOUND + userId);
            } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new RuntimeException(INVALID_USER_REQUEST + userId);
            } else {
                throw new RuntimeException(UNKNOWN_ERROR);
            }
        }
    }
}
