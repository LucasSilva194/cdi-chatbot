package pt.cienciasinvestimento.chatbot;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import pt.cienciasinvestimento.chatbot.chat.KnowledgeBaseAnswer;
import pt.cienciasinvestimento.chatbot.chat.KnowledgeBaseService;
import pt.cienciasinvestimento.chatbot.domain.Intent;

class KnowledgeBaseServiceTest {

    private final KnowledgeBaseService service = new KnowledgeBaseService();

    @Test
    void answersSilverMemberFaqFromLocalKnowledgeBase() {
        KnowledgeBaseAnswer answer = service.answerFor(
                Intent.SUBSCRIPTION_GENERAL,
                "A que conteúdo tenho direito após a minha subscrição do Silver Member?"
        );

        assertThat(answer.message())
                .contains("Lives de Análise Semanal")
                .contains("duas transmissões")
                .contains("Economics Classes");
    }

    @Test
    void answersMultibancoSubscriptionFaqFromLocalKnowledgeBase() {
        KnowledgeBaseAnswer answer = service.answerFor(
                Intent.PAYMENT_GENERAL,
                "Quero fazer uma subscrição, mas gostaria de pagar por referência multibanco. Como faço?"
        );

        assertThat(answer.message()).contains("info@cienciasdoinvestimento.com");
    }

    @Test
    void answersAvailableTrainingsWithCurrentSiteOffer() {
        KnowledgeBaseAnswer answer = service.answerFor(
                Intent.TRAINING_INFO,
                "Que formações têm disponíveis?"
        );

        assertThat(answer.message())
                .contains("Silver Member")
                .contains("Trader I #FCT")
                .contains("Master Mind #FCT")
                .contains("Análise Técnica I #FCT");
    }

    @Test
    void answersCertificationAsDgertCertified() {
        KnowledgeBaseAnswer answer = service.answerFor(
                Intent.CERTIFICATION,
                "As formações têm certificado?"
        );

        assertThat(answer.message())
                .contains("DGERT")
                .contains("Direção-Geral do Emprego e das Relações de Trabalho");
    }

    @Test
    void answersTraderIIWithoutMatchingTraderI() {
        KnowledgeBaseAnswer answer = service.answerFor(
                Intent.TRAINING_INFO,
                "O que é o Trader II?"
        );

        assertThat(answer.message())
                .contains("Trader II #FCT")
                .contains("obrigatório ter concluído Trader I");
    }

    @Test
    void answersIrsAndVatInformationForTrainingInvoices() {
        KnowledgeBaseAnswer answer = service.answerFor(
                Intent.PAYMENT_GENERAL,
                "A formação pode entrar no IRS e a fatura tem IVA?"
        );

        assertThat(answer.message())
                .contains("isenção")
                .contains("artigo 9.º do Código do IVA")
                .contains("deduzir 30%")
                .contains("67,50 €")
                .contains("60,75 €");
    }

    @Test
    void answersPrivateVideoPlaybackTroubleshooting() {
        KnowledgeBaseAnswer answer = service.answerFor(
                Intent.ACCESS_GENERAL,
                "Os vídeos do Mastermind aparecem como privados no YouTube"
        );

        assertThat(answer.message())
                .contains("Vamos resolver por passos")
                .contains("subscrição está ativa");
    }
}
