package pt.cienciasinvestimento.chatbot.chat;

import java.util.Optional;

public interface ConversationStateStore {

    Optional<ConversationFlowStep> getVideoTroubleshootingStep(String conversationId);

    void setVideoTroubleshootingStep(String conversationId, ConversationFlowStep step);

    void clearVideoTroubleshootingStep(String conversationId);
}
