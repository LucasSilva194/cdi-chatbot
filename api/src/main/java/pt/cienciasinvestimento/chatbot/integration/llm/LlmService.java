package pt.cienciasinvestimento.chatbot.integration.llm;

/**
 * Integracao futura apenas para gerar respostas. Um LLM nunca deve receber
 * ferramentas ou permissoes para criar, alterar, apagar ou submeter dados.
 */
public interface LlmService {

    LlmResponse generateSupportReply(LlmPrompt prompt);
}
