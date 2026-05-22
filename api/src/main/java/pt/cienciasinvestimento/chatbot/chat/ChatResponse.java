package pt.cienciasinvestimento.chatbot.chat;

import java.util.List;
import pt.cienciasinvestimento.chatbot.domain.Intent;

public record ChatResponse(
        String conversationId,
        Intent intent,
        String message,
        boolean escalated,
        String escalationReason,
        List<String> suggestions
) {
}
