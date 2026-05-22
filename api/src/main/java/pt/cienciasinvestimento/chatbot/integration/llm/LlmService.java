package pt.cienciasinvestimento.chatbot.integration.llm;

public interface LlmService {

    LlmResponse generateSupportReply(LlmPrompt prompt);
}
