package anas.kazay.chatapp.repository;

import anas.kazay.chatapp.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByUserId(String userId);
}
