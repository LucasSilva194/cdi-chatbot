package pt.cienciasinvestimento.chatbot.integration.llm;

public record LlmResponse(
        String message,
        boolean safetyBlocked
) {
}
