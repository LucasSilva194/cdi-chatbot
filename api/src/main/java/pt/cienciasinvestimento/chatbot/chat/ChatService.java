package pt.cienciasinvestimento.chatbot.chat;

import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pt.cienciasinvestimento.chatbot.domain.Intent;

@Service
public class ChatService {

    private final IntentClassifierService intentClassifierService;
    private final KnowledgeBaseService knowledgeBaseService;
    private final SafetyGuardService safetyGuardService;
    private final EscalationService escalationService;
    private final ConversationLogService conversationLogService;

    public ChatService(
            IntentClassifierService intentClassifierService,
            KnowledgeBaseService knowledgeBaseService,
            SafetyGuardService safetyGuardService,
            EscalationService escalationService,
            ConversationLogService conversationLogService
    ) {
        this.intentClassifierService = intentClassifierService;
        this.knowledgeBaseService = knowledgeBaseService;
        this.safetyGuardService = safetyGuardService;
        this.escalationService = escalationService;
        this.conversationLogService = conversationLogService;
    }

    public ChatResponse reply(ChatRequest request) {
        String conversationId = StringUtils.hasText(request.conversationId())
                ? request.conversationId()
                : UUID.randomUUID().toString();

        SafetyAssessment safetyAssessment = safetyGuardService.assess(request.message());
        if (safetyAssessment.blocked()) {
            KnowledgeBaseAnswer answer = knowledgeBaseService.answerFor(safetyAssessment.intent(), request.message());
            ChatResponse response = new ChatResponse(
                    conversationId,
                    safetyAssessment.intent(),
                    safetyAssessment.message(),
                    false,
                    null,
                    answer.suggestions()
            );
            conversationLogService.log(conversationId, request.message(), response.intent(), response.message(), response.escalated(), response.escalationReason());
            return response;
        }

        Intent intent = intentClassifierService.classify(request.message());
        boolean escalated = escalationService.requiresEscalation(intent, request.message());
        KnowledgeBaseAnswer answer = escalated
                ? escalationService.escalationAnswer(intent)
                : knowledgeBaseService.answerFor(intent, request.message());

        String escalationReason = escalated ? escalationService.reasonFor(intent) : null;
        ChatResponse response = new ChatResponse(
                conversationId,
                intent,
                answer.message(),
                escalated,
                escalationReason,
                answer.suggestions()
        );

        conversationLogService.log(conversationId, request.message(), response.intent(), response.message(), response.escalated(), response.escalationReason());
        return response;
    }
}
