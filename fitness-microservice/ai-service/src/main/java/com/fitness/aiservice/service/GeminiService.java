package com.fitness.aiservice.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

import static com.fitness.aiservice.constant.AIServiceConstants.GEMINI_MODEL;

@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final WebClient.Builder webClientBuilder;

    public String geminiAnswerRest(String prompt) {
        Map<String, Object> requestBody = Map.of("contents", new Object[]{
                Map.of("parts", new Object[]{
                        Map.of("text", prompt)}
                )}
        );

        WebClient webClient = webClientBuilder.build();

        return webClient.post()
                .uri(geminiApiUrl + "?key=" + geminiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String geminiAnswerLib(String prompt) {
        Client client = new Client(); // Will read GEMINI_API_KEY from env var.

        GenerateContentResponse response = client.models.generateContent(
                GEMINI_MODEL,
                prompt,
                null
        );
        return response.text();
    }
}
