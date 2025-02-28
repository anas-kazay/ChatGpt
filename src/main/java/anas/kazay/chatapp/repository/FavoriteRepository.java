package anas.kazay.chatapp.repository;

import anas.kazay.chatapp.model.Favorite;
import anas.kazay.chatapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUser(User user);
    boolean existsByUserAndConversationId(User user, Long conversationId);
}