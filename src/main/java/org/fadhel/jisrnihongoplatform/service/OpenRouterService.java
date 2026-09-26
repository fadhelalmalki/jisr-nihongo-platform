package org.fadhel.jisrnihongoplatform.service;


import org.fadhel.jisrnihongoplatform.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class OpenRouterService {

    @Value("${openrouter.api.key}")
    private String apiKey;

    @Value("${openrouter.api.url}")
    private String apiUrl;

    private final RestClient restClient;

    public OpenRouterService() {
        this.restClient = RestClient.create();
    }

    /**
     * Sends any custom prompt to OpenRouter free models and returns the text response.
     */
    public String askAi(String prompt) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new ApiException("OpenRouter API key is not configured.");
        }

        // Construct request payload following OpenAI / OpenRouter schema
        Map<String, Object> requestBody = Map.of(
                "model", "openrouter/free", // Uses available free models automatically
                "messages", List.of(
                        Map.of("role", "system", "content", "You are a helpful Japanese language teaching assistant for the Jisr Nihongo Platform."),
                        Map.of("role", "user", "content", prompt)
                )
        );

        try {
            Map<String, Object> response = restClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("HTTP-Referer", "http://localhost:8080") // Optional requirement for OpenRouter
                    .header("X-Title", "Jisr Nihongo Platform")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            // Extract completion string from JSON response
            if (response != null && response.containsKey("choices")) {
                List<?> choices = (List<?>) response.get("choices");
                if (!choices.isEmpty()) {
                    Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
                    Map<?, ?> message = (Map<?, ?>) firstChoice.get("message");
                    return (String) message.get("content");
                }
            }
            throw new ApiException("Empty response received from AI provider.");

        } catch (Exception e) {
            throw new ApiException("AI Service Error: " + e.getMessage());
        }
    }

}
