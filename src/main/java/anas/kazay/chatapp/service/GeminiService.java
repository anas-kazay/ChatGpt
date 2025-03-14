package anas.kazay.chatapp.service;

import anas.kazay.chatapp.model.Conversation;
import anas.kazay.chatapp.repository.ConversationRepository;
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
public class GeminiService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final ConversationRepository conversationRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public GeminiService(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    public String generateResponse(String userQuestion, String userId) {
        logger.info("🔹 Using Gemini API Key: {}", geminiApiKey.substring(0, 5) + "*****");

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + geminiApiKey;
        logger.info("🔹 Gemini API URL: {}", url);

        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> content = new HashMap<>();
        List<Map<String, Object>> parts = new ArrayList<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", "Provide a concise answer in a single paragraph of 3 to 5 lines:"+userQuestion);
        parts.add(part);
        content.put("parts", parts);
        contents.add(content);
        requestBody.put("contents", contents);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
            logger.info("🔹 Gemini API Response: {}", response.getBody());

            if (response.getBody() == null || !response.getBody().containsKey("candidates")) {
                throw new RuntimeException("Invalid response from Gemini API");
            }

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                throw new RuntimeException("No candidates found in Gemini API response");
            }

            Map<String, Object> candidate = candidates.get(0);
            Map<String, Object> contentResponse = (Map<String, Object>) candidate.get("content");
            if (contentResponse == null || !contentResponse.containsKey("parts")) {
                throw new RuntimeException("No content parts found in Gemini API response");
            }

            List<Map<String, Object>> partsResponse = (List<Map<String, Object>>) contentResponse.get("parts");
            if (partsResponse == null || partsResponse.isEmpty()) {
                throw new RuntimeException("No parts found in Gemini API response");
            }

            Map<String, Object> partResponse = partsResponse.get(0);
            String responseText = (String) partResponse.get("text");

            Conversation conversation = new Conversation();
            conversation.setUserQuestion(userQuestion);
            conversation.setGptResponse(responseText);
            conversation.setUserId(userId);

            conversationRepository.save(conversation);

            return responseText;

        } catch (Exception e) {
            logger.error("❌ Error calling Gemini API", e);
            throw new RuntimeException("Failed to get response from Gemini API");
        }
    }
}