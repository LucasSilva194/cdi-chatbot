package pt.cienciasinvestimento.chatbot.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(
        @Size(max = 120) String conversationId,
        @NotBlank @Size(max = 4000) String message
) {
}
