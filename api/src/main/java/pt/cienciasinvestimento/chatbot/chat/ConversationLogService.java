package pt.cienciasinvestimento.chatbot.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import pt.cienciasinvestimento.chatbot.domain.Intent;

@Service
public class ConversationLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConversationLogService.class);

    private final ConversationLogRepository repository;

    public ConversationLogService(ConversationLogRepository repository) {
        this.repository = repository;
    }

    public void log(String conversationId, String userMessage, Intent intent, String botMessage, boolean escalated, String escalationReason) {
        try {
            repository.save(new ConversationLogEntry(
                    conversationId,
                    userMessage,
                    intent,
                    botMessage,
                    escalated,
                    escalationReason
            ));
        } catch (DataAccessException exception) {
            LOGGER.warn("Could not persist conversation log for conversationId={}", conversationId, exception);
        }
    }
}
