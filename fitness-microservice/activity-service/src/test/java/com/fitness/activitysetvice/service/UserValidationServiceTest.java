package com.fitness.activitysetvice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserValidationServiceTest {

    private WebClient webClient;

    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    private WebClient.ResponseSpec responseSpec;

    private UserValidationService userValidationService;

    @BeforeEach
    void setUp() {
        webClient = mock(WebClient.class);
        requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);

        userValidationService = new UserValidationService(webClient);
    }

    @Test
    void shouldReturnTrueWhenUserIsValid() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/api/users/{userId}/validate", "user123")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(true));

        boolean result = userValidationService.validateUser("user123");

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenUserIsInvalid() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/api/users/{userId}/validate", "user123")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(false));

        boolean result = userValidationService.validateUser("user123");

        assertThat(result).isFalse();
    }

    @Test
    void shouldThrowRuntimeExceptionWhenUserNotFound() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/api/users/{userId}/validate", "user123")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenThrow(
                new WebClientResponseException(HttpStatus.NOT_FOUND.value(), "Not Found", null, null, null)
        );

        assertThatThrownBy(() -> userValidationService.validateUser("user123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not Found");
    }

    @Test
    void shouldThrowRuntimeExceptionWhenBadRequest() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/api/users/{userId}/validate", "user123")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenThrow(
                new WebClientResponseException(HttpStatus.BAD_REQUEST.value(), "Bad Request", null, null, null)
        );

        assertThatThrownBy(() -> userValidationService.validateUser("user123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid request");
    }

    @Test
    void shouldThrowGenericRuntimeExceptionOnOtherErrors() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/api/users/{userId}/validate", "user123")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenThrow(
                new WebClientResponseException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Error", null, null, null)
        );

        assertThatThrownBy(() -> userValidationService.validateUser("user123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Something went wrong");
    }
}
