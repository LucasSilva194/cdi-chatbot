package pt.cienciasinvestimento.chatbot;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import pt.cienciasinvestimento.chatbot.chat.InMemoryConversationStateStore;
import pt.cienciasinvestimento.chatbot.chat.KnowledgeBaseAnswer;
import pt.cienciasinvestimento.chatbot.chat.VideoTroubleshootingService;

class VideoTroubleshootingServiceTest {

    private final VideoTroubleshootingService service = new VideoTroubleshootingService(
            new InMemoryConversationStateStore(Duration.ofHours(1))
    );

    @Test
    void guidesComputerUserToBrowserSpecificInstructions() {
        KnowledgeBaseAnswer first = service.handle("conversation-1", "Não consigo ver os vídeos do Trader I").orElseThrow();
        assertThat(first.message()).contains("subscrição está ativa");

        KnowledgeBaseAnswer active = service.handle("conversation-1", "Sim, está ativa").orElseThrow();
        assertThat(active.message()).contains("telemóvel/tablet").contains("computador");

        KnowledgeBaseAnswer computer = service.handle("conversation-1", "Computador").orElseThrow();
        assertThat(computer.message()).contains("sessão iniciada no YouTube");

        KnowledgeBaseAnswer youtube = service.handle("conversation-1", "Sim, tenho sessão iniciada").orElseThrow();
        assertThat(youtube.message()).contains("Qual é o browser");

        KnowledgeBaseAnswer chrome = service.handle("conversation-1", "Chrome").orElseThrow();
        assertThat(chrome.message())
                .contains("No Chrome")
                .contains("Cookies de terceiros")
                .doesNotContain("No Edge")
                .doesNotContain("No Firefox")
                .doesNotContain("No Brave");
    }

    @Test
    void givesMobileInstructionsOnlyForMobileUsers() {
        service.handle("conversation-2", "O vídeo do Mastermind aparece como privado").orElseThrow();
        service.handle("conversation-2", "Sim, está ativa").orElseThrow();

        KnowledgeBaseAnswer mobile = service.handle("conversation-2", "Telemóvel").orElseThrow();

        assertThat(mobile.message())
                .contains("No telemóvel/tablet")
                .contains("browser")
                .contains("não a app do YouTube")
                .doesNotContain("No Chrome")
                .doesNotContain("No Edge");
    }

    @Test
    void explainsInactiveSubscriptionAsLikelyCause() {
        service.handle("conversation-3", "Não consigo ver os vídeos do Trader I").orElseThrow();

        KnowledgeBaseAnswer inactive = service.handle("conversation-3", "Não, não está ativa").orElseThrow();

        assertThat(inactive.message())
                .contains("muito provável")
                .contains("subscrição ou acesso ativo");
    }
}
