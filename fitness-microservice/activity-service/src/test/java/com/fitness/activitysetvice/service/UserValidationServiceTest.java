package com.fitness.activitysetvice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Optional;

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
    void shouldReturnTrueIfValid() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), Optional.ofNullable(any()))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(true));

        boolean result = userValidationService.validateUser("u1");

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseIfInvalid() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), Optional.ofNullable(any()))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(false));

        boolean result = userValidationService.validateUser("u1");

        assertThat(result).isFalse();
    }

    @Test
    void shouldThrowNotFound() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), Optional.ofNullable(any()))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenThrow(
                new WebClientResponseException(HttpStatus.NOT_FOUND.value(), "not found", null, null, null));

        assertThatThrownBy(() -> userValidationService.validateUser("bad"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not Found");
    }

    @Test
    void shouldThrowBadRequest() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), Optional.ofNullable(any()))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenThrow(
                new WebClientResponseException(HttpStatus.BAD_REQUEST.value(), "bad request", null, null, null));

        assertThatThrownBy(() -> userValidationService.validateUser("bad"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid request");
    }

    @Test
    void shouldThrowGenericError() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), Optional.ofNullable(any()))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenThrow(
                new WebClientResponseException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "error", null, null, null));

        assertThatThrownBy(() -> userValidationService.validateUser("err"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Something went wrong");
    }
}
