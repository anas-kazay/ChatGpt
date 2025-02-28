package anas.kazay.chatapp.service;

import anas.kazay.chatapp.model.User;

public interface AIService {
    String generateResponse(String userQuestion, User user, Long modelId);
}
