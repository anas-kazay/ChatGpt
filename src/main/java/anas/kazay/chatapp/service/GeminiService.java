package anas.kazay.chatapp.service;

import anas.kazay.chatapp.model.Conversation;
import anas.kazay.chatapp.model.Model;
import anas.kazay.chatapp.model.User;
import anas.kazay.chatapp.repository.ConversationRepository;
import anas.kazay.chatapp.repository.ModelRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService implements AIService{

    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final ConversationRepository conversationRepository;
    private final ModelRepository modelRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public GeminiService(ConversationRepository conversationRepository, ModelRepository modelRepository) {
        this.conversationRepository = conversationRepository;
        this.modelRepository = modelRepository;
    }

    @Override
    public String generateResponse(String userQuestion, User user, Long modelId) {
        // Get the model from the repository
        Model model = modelRepository.findById(modelId)
                .orElseThrow(() -> new RuntimeException("Model not found"));

        if (!model.isActive()) {
            throw new RuntimeException("This model is currently unavailable");
        }

        // Log the API key (first 5 chars + asterisks for security)
        logger.info("🔹 Using API Key: {}", geminiApiKey.substring(0, 5) + "*****");

        // Build the API URL based on the model
        String url = model.getApiEndpoint() + "?key=" + geminiApiKey;
        logger.info("🔹 API URL: {}", url);

        // Setup request body
        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> content = new HashMap<>();
        List<Map<String, Object>> parts = new ArrayList<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", "Provide a concise answer in a single paragraph of 3 to 5 lines:" + userQuestion);
        parts.add(part);
        content.put("parts", parts);
        contents.add(content);
        requestBody.put("contents", contents);

        // Setup headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
            logger.info("🔹 API Response: {}", response.getBody());

            if (response.getBody() == null || !response.getBody().containsKey("candidates")) {
                throw new RuntimeException("Invalid response from API");
            }

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                throw new RuntimeException("No candidates found in API response");
            }

            Map<String, Object> candidate = candidates.get(0);
            Map<String, Object> contentResponse = (Map<String, Object>) candidate.get("content");
            if (contentResponse == null || !contentResponse.containsKey("parts")) {
                throw new RuntimeException("No content parts found in API response");
            }

            List<Map<String, Object>> partsResponse = (List<Map<String, Object>>) contentResponse.get("parts");
            if (partsResponse == null || partsResponse.isEmpty()) {
                throw new RuntimeException("No parts found in API response");
            }

            Map<String, Object> partResponse = partsResponse.get(0);
            String responseText = (String) partResponse.get("text");

            // Save the conversation
            Conversation conversation = new Conversation();
            conversation.setUserQuestion(userQuestion);
            conversation.setAiResponse(responseText);
            conversation.setUser(user);
            conversation.setModel(model);

            conversationRepository.save(conversation);

            return responseText;

        } catch (Exception e) {
            logger.error("❌ Error calling API", e);
            throw new RuntimeException("Failed to get response from API: " + e.getMessage());
        }
    }
}