package com.fitness.aiservice.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;
    //TODO cannot find values from config

    private final WebClient webClient;

    public GeminiService(WebClient.Builder webClient){
        this.webClient = WebClient.builder().build();
    }

    /**
     * This method uses WebClient ensure u have GEMINI_API_KEY and GEMINI_API_URl in the config file
     * */
    public String geminiAnswerRest(String prompt){
        Map<String, Object> requestBody = Map.of("contents", new Object[]{
                Map.of("parts", new Object[]{
                        Map.of("text", prompt)}
                )}
        );

        String response = webClient.post()
                .uri("geminiApiUrl" + "?key=" + "geminiApiKey")
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return response;
    }


    /**
     * This method uses google-genai ensure u have GEMINI_API_KEY as environment variable
     * */
    public String geminiAnswerLib(String prompt){

        // The client gets the API key from the environment variable `GEMINI_API_KEY`.
        Client client = new Client();

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        prompt,
                        null);

        return response.text();
    }

}
