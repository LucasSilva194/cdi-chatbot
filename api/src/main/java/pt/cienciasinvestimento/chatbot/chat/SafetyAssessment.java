package pt.cienciasinvestimento.chatbot.chat;

import pt.cienciasinvestimento.chatbot.domain.Intent;

public record SafetyAssessment(
        boolean blocked,
        Intent intent,
        String message
) {
    public static SafetyAssessment allowed() {
        return new SafetyAssessment(false, null, null);
    }

    public static SafetyAssessment blocked(Intent intent, String message) {
        return new SafetyAssessment(true, intent, message);
    }
}
