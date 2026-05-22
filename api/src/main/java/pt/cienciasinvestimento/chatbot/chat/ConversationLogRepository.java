package pt.cienciasinvestimento.chatbot.chat;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationLogRepository extends JpaRepository<ConversationLogEntry, UUID> {
}
