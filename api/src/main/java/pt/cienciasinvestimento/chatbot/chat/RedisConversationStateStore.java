package pt.cienciasinvestimento.chatbot.chat;

import java.time.Duration;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.conversation-state.store", havingValue = "redis")
public class RedisConversationStateStore implements ConversationStateStore {

    private static final String VIDEO_KEY_PREFIX = "cdi-chatbot:conversation:video:";

    private final StringRedisTemplate redisTemplate;
    private final Duration ttl;

    public RedisConversationStateStore(
            StringRedisTemplate redisTemplate,
            @Value("${app.conversation-state.ttl:PT2H}") Duration ttl
    ) {
        this.redisTemplate = redisTemplate;
        this.ttl = ttl;
    }

    @Override
    public Optional<ConversationFlowStep> getVideoTroubleshootingStep(String conversationId) {
        String value = redisTemplate.opsForValue().get(key(conversationId));
        if (value == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(ConversationFlowStep.valueOf(value));
        } catch (IllegalArgumentException exception) {
            clearVideoTroubleshootingStep(conversationId);
            return Optional.empty();
        }
    }

    @Override
    public void setVideoTroubleshootingStep(String conversationId, ConversationFlowStep step) {
        redisTemplate.opsForValue().set(key(conversationId), step.name(), ttl);
    }

    @Override
    public void clearVideoTroubleshootingStep(String conversationId) {
        redisTemplate.delete(key(conversationId));
    }

    private String key(String conversationId) {
        return VIDEO_KEY_PREFIX + conversationId;
    }
}
