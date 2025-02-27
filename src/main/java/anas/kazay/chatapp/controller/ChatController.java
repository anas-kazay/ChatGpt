package anas.kazay.chatapp.controller;

import anas.kazay.chatapp.model.Conversation;
import anas.kazay.chatapp.service.GeminiService;
import anas.kazay.chatapp.repository.ConversationRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final GeminiService geminiService;
    private final ConversationRepository conversationRepository;

    public ChatController(GeminiService geminiService, ConversationRepository conversationRepository) {
        this.geminiService = geminiService;
        this.conversationRepository = conversationRepository;
    }

    @PostMapping("/prompts")
    public Map<String, String> getResponse(@RequestBody Map<String, String> payload) {
        String userQuestion = payload.get("question");
        String userId = payload.get("userId");

        String response = geminiService.generateResponse(userQuestion, userId);

        return Map.of("response", response);
    }

    @GetMapping("/conversations")
    public List<Conversation> getUserConversations(@RequestParam String userId) {
        return conversationRepository.findByUserId(userId);
    }
}
