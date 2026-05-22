package pt.cienciasinvestimento.chatbot.integration.llm;

import pt.cienciasinvestimento.chatbot.domain.Intent;

public record LlmPrompt(
        String userMessage,
        Intent intent,
        String knowledgeContext
) {
}
