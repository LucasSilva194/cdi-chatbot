package pt.cienciasinvestimento.chatbot;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import pt.cienciasinvestimento.chatbot.chat.IntentClassifierService;
import pt.cienciasinvestimento.chatbot.domain.Intent;

class IntentClassifierServiceTest {

    private final IntentClassifierService service = new IntentClassifierService();

    @Test
    void classifiesTrainingInformation() {
        assertThat(service.classify("Que formações têm disponíveis?")).isEqualTo(Intent.TRAINING_INFO);
    }

    @Test
    void classifiesPasswordRecovery() {
        assertThat(service.classify("Como recupero a password?")).isEqualTo(Intent.PASSWORD_RECOVERY);
    }

    @Test
    void classifiesPersonalPaymentIssue() {
        assertThat(service.classify("Paguei mas ainda não tenho acesso")).isEqualTo(Intent.PERSONAL_ACCOUNT_ISSUE);
    }

    @Test
    void classifiesFinancialAdviceAsBlocked() {
        assertThat(service.classify("Devo investir neste ETF?")).isEqualTo(Intent.FINANCIAL_ADVICE_BLOCKED);
    }
}
