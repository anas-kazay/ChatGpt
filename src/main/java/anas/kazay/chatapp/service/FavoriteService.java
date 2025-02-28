package anas.kazay.chatapp.service;

import anas.kazay.chatapp.dto.FavoriteRequest;
import anas.kazay.chatapp.model.Conversation;
import anas.kazay.chatapp.model.Favorite;
import anas.kazay.chatapp.model.User;
import anas.kazay.chatapp.repository.ConversationRepository;
import anas.kazay.chatapp.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ConversationRepository conversationRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, ConversationRepository conversationRepository) {
        this.favoriteRepository = favoriteRepository;
        this.conversationRepository = conversationRepository;
    }

    public Favorite addFavorite(User user, FavoriteRequest request) {
        // Check if conversation exists and belongs to the user
        Conversation conversation = conversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        if (!conversation.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Conversation does not belong to the user");
        }

        // Check if already favorited
        if (favoriteRepository.existsByUserAndConversationId(user, request.getConversationId())) {
            throw new RuntimeException("Conversation is already in favorites");
        }

        // Create and save favorite
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setConversation(conversation);

        return favoriteRepository.save(favorite);
    }

    public void removeFavorite(User user, Long favoriteId) {
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new RuntimeException("Favorite not found"));

        if (!favorite.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Favorite does not belong to the user");
        }

        favoriteRepository.delete(favorite);
    }

    public List<Favorite> getUserFavorites(User user) {
        return favoriteRepository.findByUser(user);
    }
}
