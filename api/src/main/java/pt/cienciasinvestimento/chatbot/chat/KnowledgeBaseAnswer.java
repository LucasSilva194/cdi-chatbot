package pt.cienciasinvestimento.chatbot.chat;

import java.util.List;

public record KnowledgeBaseAnswer(
        String message,
        List<String> suggestions
) {
}
