package pt.cienciasinvestimento.chatbot;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import pt.cienciasinvestimento.chatbot.chat.SafetyAssessment;
import pt.cienciasinvestimento.chatbot.chat.SafetyGuardService;
import pt.cienciasinvestimento.chatbot.domain.Intent;

class SafetyGuardServiceTest {

    private final SafetyGuardService service = new SafetyGuardService();

    @Test
    void blocksInvestmentRecommendationRequests() {
        SafetyAssessment assessment = service.assess("Devo investir neste ETF?");

        assertThat(assessment.blocked()).isTrue();
        assertThat(assessment.intent()).isEqualTo(Intent.FINANCIAL_ADVICE_BLOCKED);
        assertThat(assessment.message()).contains("Não posso dar aconselhamento financeiro");
    }

    @Test
    void allowsEducationalCourseQuestionsAboutInvesting() {
        SafetyAssessment assessment = service.assess("Qual o melhor curso para começar a aprender sobre investimento?");

        assertThat(assessment.blocked()).isFalse();
    }
}
