package anas.kazay.chatapp.controller;

import anas.kazay.chatapp.dto.PromptRequest;
import anas.kazay.chatapp.model.Conversation;
import anas.kazay.chatapp.model.User;
import anas.kazay.chatapp.service.AIService;
import anas.kazay.chatapp.service.UserService;
import anas.kazay.chatapp.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final AIService aiService;
    private final ConversationRepository conversationRepository;
    private final UserService userService;

    public ChatController(AIService aiService, ConversationRepository conversationRepository, UserService userService) {
        this.aiService = aiService;
        this.conversationRepository = conversationRepository;
        this.userService = userService;
    }

    @PostMapping("/prompt")
    public ResponseEntity<Map<String, String>> getResponse(@RequestBody PromptRequest request) {
        User currentUser = userService.getCurrentUser();
        String response = aiService.generateResponse(request.getQuestion(), currentUser, request.getModelId());
        return ResponseEntity.ok(Map.of("response", response));
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<Conversation>> getUserConversations() {
        User currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(conversationRepository.findByUserOrderByTimestampDesc(currentUser));
    }

    @GetMapping("/conversation/{id}")
    public ResponseEntity<Conversation> getConversation(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        Conversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        if (!conversation.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Conversation does not belong to the user");
        }

        return ResponseEntity.ok(conversation);
    }

    @DeleteMapping("/conversation/{id}")
    public ResponseEntity<Void> deleteConversation(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        Conversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        if (!conversation.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Conversation does not belong to the user");
        }

        conversationRepository.delete(conversation);
        return ResponseEntity.noContent().build();
    }
}