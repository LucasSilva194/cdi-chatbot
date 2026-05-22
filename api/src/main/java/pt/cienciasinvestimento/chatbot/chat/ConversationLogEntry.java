package pt.cienciasinvestimento.chatbot.chat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import pt.cienciasinvestimento.chatbot.domain.Intent;

@Entity
@Table(name = "conversation_logs")
public class ConversationLogEntry {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 120)
    private String conversationId;

    @Column(nullable = false, length = 4000)
    private String userMessage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 80)
    private Intent intent;

    @Column(nullable = false, length = 4000)
    private String botMessage;

    @Column(nullable = false)
    private boolean escalated;

    @Column(length = 500)
    private String escalationReason;

    @Column(nullable = false)
    private Instant createdAt;

    protected ConversationLogEntry() {
    }

    public ConversationLogEntry(String conversationId, String userMessage, Intent intent, String botMessage, boolean escalated, String escalationReason) {
        this.conversationId = conversationId;
        this.userMessage = userMessage;
        this.intent = intent;
        this.botMessage = botMessage;
        this.escalated = escalated;
        this.escalationReason = escalationReason;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getConversationId() {
        return conversationId;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public Intent getIntent() {
        return intent;
    }

    public String getBotMessage() {
        return botMessage;
    }

    public boolean isEscalated() {
        return escalated;
    }

    public String getEscalationReason() {
        return escalationReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
