package pt.cienciasinvestimento.chatbot.chat;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.conversation-state.store", havingValue = "memory", matchIfMissing = true)
public class InMemoryConversationStateStore implements ConversationStateStore {

    private final ConcurrentMap<String, Entry> videoStepsByConversation = new ConcurrentHashMap<>();
    private final Duration ttl;
    private final Clock clock;

    @Autowired
    public InMemoryConversationStateStore(@Value("${app.conversation-state.ttl:PT2H}") Duration ttl) {
        this(ttl, Clock.systemUTC());
    }

    InMemoryConversationStateStore(Duration ttl, Clock clock) {
        this.ttl = ttl;
        this.clock = clock;
    }

    @Override
    public Optional<ConversationFlowStep> getVideoTroubleshootingStep(String conversationId) {
        Entry entry = videoStepsByConversation.get(conversationId);
        if (entry == null) {
            return Optional.empty();
        }

        if (entry.expiresAt().isBefore(Instant.now(clock))) {
            videoStepsByConversation.remove(conversationId);
            return Optional.empty();
        }

        return Optional.of(entry.step());
    }

    @Override
    public void setVideoTroubleshootingStep(String conversationId, ConversationFlowStep step) {
        videoStepsByConversation.put(conversationId, new Entry(step, Instant.now(clock).plus(ttl)));
    }

    @Override
    public void clearVideoTroubleshootingStep(String conversationId) {
        videoStepsByConversation.remove(conversationId);
    }

    private record Entry(ConversationFlowStep step, Instant expiresAt) {
    }
}
