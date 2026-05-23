package pt.cienciasinvestimento.chatbot.chat;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class VideoTroubleshootingService {

    private final ConversationStateStore conversationStateStore;

    public VideoTroubleshootingService(ConversationStateStore conversationStateStore) {
        this.conversationStateStore = conversationStateStore;
    }

    public Optional<KnowledgeBaseAnswer> handle(String conversationId, String message) {
        String normalized = TextNormalizer.normalize(message);

        if (asksForHumanSupport(normalized)) {
            conversationStateStore.clearVideoTroubleshootingStep(conversationId);
            return Optional.empty();
        }

        ConversationFlowStep currentStep = conversationStateStore.getVideoTroubleshootingStep(conversationId).orElse(null);
        if (currentStep == null) {
            if (!isVideoPlaybackQuestion(normalized)) {
                return Optional.empty();
            }

            conversationStateStore.setVideoTroubleshootingStep(conversationId, ConversationFlowStep.VIDEO_ASK_ACTIVE_SUBSCRIPTION);
            return Optional.of(new KnowledgeBaseAnswer(
                    "Vamos resolver por passos. A sua subscrição está ativa?",
                    List.of("Sim, está ativa", "Não, não está ativa", "Não sei")
            ));
        }

        return Optional.of(answerForStep(conversationId, currentStep, normalized));
    }

    private KnowledgeBaseAnswer answerForStep(String conversationId, ConversationFlowStep step, String normalized) {
        return switch (step) {
            case VIDEO_ASK_ACTIVE_SUBSCRIPTION -> answerActiveSubscription(conversationId, normalized);
            case VIDEO_ASK_DEVICE -> answerDevice(conversationId, normalized);
            case VIDEO_ASK_YOUTUBE_LOGIN -> answerYoutubeLogin(conversationId, normalized);
            case VIDEO_ASK_BROWSER -> answerBrowser(conversationId, normalized);
        };
    }

    private KnowledgeBaseAnswer answerActiveSubscription(String conversationId, String normalized) {
        if (isNo(normalized)) {
            conversationStateStore.clearVideoTroubleshootingStep(conversationId);
            return new KnowledgeBaseAnswer(
                    "Então é muito provável que seja esse o problema. Os vídeos privados só ficam disponíveis para clientes com subscrição ou acesso ativo à formação. Se acredita que devia ter acesso, contacte info@cienciasdoinvestimento.com para a equipa confirmar.",
                    List.of("Falar com suporte", "Que formações têm disponíveis?", "Como recupero a password?")
            );
        }

        if (isYes(normalized)) {
            conversationStateStore.setVideoTroubleshootingStep(conversationId, ConversationFlowStep.VIDEO_ASK_DEVICE);
            return new KnowledgeBaseAnswer(
                    "Certo. Está a tentar ver os vídeos pelo telemóvel/tablet ou pelo computador?",
                    List.of("Telemóvel/tablet", "Computador")
            );
        }

        return new KnowledgeBaseAnswer(
                "Para perceber a causa, preciso só de confirmar primeiro: a sua subscrição está ativa?",
                List.of("Sim, está ativa", "Não, não está ativa", "Não sei")
        );
    }

    private KnowledgeBaseAnswer answerDevice(String conversationId, String normalized) {
        if (containsAny(normalized, List.of("telemovel", "telefone", "mobile", "tablet", "ipad", "android", "iphone", "ios"))) {
            conversationStateStore.clearVideoTroubleshootingStep(conversationId);
            return new KnowledgeBaseAnswer(
                    "No telemóvel/tablet, o mais importante é usar o browser e não a app do YouTube. Abra o YouTube no mesmo browser onde está a usar o site da CDI, faça login com o email que tem acesso à formação, volte à área de cliente da CDI no mesmo browser e recarregue a página. Se continuar a aparecer como privado, contacte info@cienciasdoinvestimento.com.",
                    List.of("Falar com suporte", "Como recupero a password?", "Que formações têm disponíveis?")
            );
        }

        if (containsAny(normalized, List.of("computador", "pc", "desktop", "portatil", "laptop", "mac", "windows"))) {
            conversationStateStore.setVideoTroubleshootingStep(conversationId, ConversationFlowStep.VIDEO_ASK_YOUTUBE_LOGIN);
            return new KnowledgeBaseAnswer(
                    "No computador, tem sessão iniciada no YouTube, no mesmo browser, com o email que tem acesso à formação?",
                    List.of("Sim, tenho sessão iniciada", "Não tenho sessão iniciada")
            );
        }

        return new KnowledgeBaseAnswer(
                "Está a tentar ver os vídeos pelo telemóvel/tablet ou pelo computador?",
                List.of("Telemóvel/tablet", "Computador")
        );
    }

    private KnowledgeBaseAnswer answerYoutubeLogin(String conversationId, String normalized) {
        if (isNo(normalized)) {
            conversationStateStore.clearVideoTroubleshootingStep(conversationId);
            return new KnowledgeBaseAnswer(
                    "Então deverá ser esse o problema. Faça login no YouTube no mesmo browser onde está a usar o site da CDI, com o email associado à formação. Depois volte à área de cliente e recarregue a página do vídeo.",
                    List.of("Falar com suporte", "Como recupero a password?")
            );
        }

        if (isYes(normalized)) {
            conversationStateStore.setVideoTroubleshootingStep(conversationId, ConversationFlowStep.VIDEO_ASK_BROWSER);
            return new KnowledgeBaseAnswer(
                    "Obrigado. Qual é o browser que está a usar?",
                    List.of("Chrome", "Edge", "Firefox", "Brave")
            );
        }

        return new KnowledgeBaseAnswer(
                "Tem sessão iniciada no YouTube, no mesmo browser, com o email que tem acesso à formação?",
                List.of("Sim, tenho sessão iniciada", "Não tenho sessão iniciada")
        );
    }

    private KnowledgeBaseAnswer answerBrowser(String conversationId, String normalized) {
        if (containsAny(normalized, List.of("chrome", "google chrome"))) {
            conversationStateStore.clearVideoTroubleshootingStep(conversationId);
            return new KnowledgeBaseAnswer(
                    "No Chrome, abra Definições > Privacidade e segurança > Cookies de terceiros. Permita cookies de terceiros ou adicione uma exceção para o site da Ciências do Investimento. Depois recarregue a página do vídeo.",
                    List.of("Falar com suporte", "Como recupero a password?")
            );
        }

        if (containsAny(normalized, List.of("edge", "microsoft edge"))) {
            conversationStateStore.clearVideoTroubleshootingStep(conversationId);
            return new KnowledgeBaseAnswer(
                    "No Edge, abra Definições > Cookies e permissões de site > Gerir e eliminar cookies e dados do site. Permita cookies e desative o bloqueio de cookies de terceiros para este caso, ou adicione uma exceção para o site da CDI. Se a prevenção de tracking estiver em modo rigoroso, use modo equilibrado ou crie exceção para o site.",
                    List.of("Falar com suporte", "Como recupero a password?")
            );
        }

        if (containsAny(normalized, List.of("firefox", "mozilla"))) {
            conversationStateStore.clearVideoTroubleshootingStep(conversationId);
            return new KnowledgeBaseAnswer(
                    "No Firefox, abra a página da CDI, clique no ícone de escudo junto ao endereço e desative a Proteção Melhorada Contra Monitorização apenas para este site. Depois recarregue a página do vídeo.",
                    List.of("Falar com suporte", "Como recupero a password?")
            );
        }

        if (containsAny(normalized, List.of("brave"))) {
            conversationStateStore.clearVideoTroubleshootingStep(conversationId);
            return new KnowledgeBaseAnswer(
                    "No Brave, abra a página da CDI, clique no ícone Brave Shields e permita cookies entre sites ou reduza a proteção dos Shields para este site. Depois recarregue a página do vídeo.",
                    List.of("Falar com suporte", "Como recupero a password?")
            );
        }

        return new KnowledgeBaseAnswer(
                "Qual é o browser que está a usar?",
                List.of("Chrome", "Edge", "Firefox", "Brave")
        );
    }

    private boolean isVideoPlaybackQuestion(String normalized) {
        return containsAny(normalized, List.of("video", "videos", "youtube"))
                && containsAny(normalized, List.of(
                "privado",
                "privados",
                "nao consigo ver",
                "nao abre",
                "bloqueia",
                "bloqueado",
                "cookies",
                "seguimento entre sites",
                "tracking",
                "trader i",
                "mastermind",
                "master mind"
        ));
    }

    private boolean asksForHumanSupport(String normalized) {
        return containsAny(normalized, List.of("falar com suporte", "suporte humano", "contactar suporte"));
    }

    private boolean isYes(String normalized) {
        return containsAny(normalized, List.of("sim", "ativa", "activo", "ativo", "tenho", "tenho acesso"));
    }

    private boolean isNo(String normalized) {
        return containsAny(normalized, List.of("nao", "não", "nao tenho", "sem subscricao", "inativa", "inactivo", "inativo"));
    }

    private boolean containsAny(String value, List<String> terms) {
        return terms.stream().anyMatch(value::contains);
    }
}
