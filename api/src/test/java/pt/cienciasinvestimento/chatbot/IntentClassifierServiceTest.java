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
    void classifiesLoginCodeAccessQuestions() {
        assertThat(service.classify("Não recebi o código de login")).isEqualTo(Intent.ACCESS_GENERAL);
        assertThat(service.classify("Como posso entrar no site?")).isEqualTo(Intent.ACCESS_GENERAL);
    }

    @Test
    void classifiesPersonalPaymentIssue() {
        assertThat(service.classify("Paguei mas ainda não tenho acesso")).isEqualTo(Intent.PERSONAL_ACCOUNT_ISSUE);
    }

    @Test
    void classifiesFinancialAdviceAsBlocked() {
        assertThat(service.classify("Devo investir neste ETF?")).isEqualTo(Intent.FINANCIAL_ADVICE_BLOCKED);
    }

    @Test
    void classifiesSilverMemberContentAsSubscriptionQuestion() {
        assertThat(service.classify("A que conteúdo tenho direito após a minha subscrição do Silver Member?"))
                .isEqualTo(Intent.SUBSCRIPTION_GENERAL);
    }

    @Test
    void classifiesImmediateAccessQuestion() {
        assertThat(service.classify("Tenho acesso ao conteúdo imediatamente?")).isEqualTo(Intent.ACCESS_GENERAL);
    }

    @Test
    void classifiesDgertAsCertificationQuestion() {
        assertThat(service.classify("As formações são certificadas pela DGERT?")).isEqualTo(Intent.CERTIFICATION);
    }

    @Test
    void classifiesIrsAndInvoiceQuestionsAsPaymentGeneral() {
        assertThat(service.classify("A formação pode entrar no IRS?")).isEqualTo(Intent.PAYMENT_GENERAL);
        assertThat(service.classify("Como funciona a faturação e o IVA?")).isEqualTo(Intent.PAYMENT_GENERAL);
    }

    @Test
    void classifiesPrivateVideoPlaybackAsAccessGeneral() {
        assertThat(service.classify("Não consigo ver os vídeos do Trader I, aparecem como privados no YouTube"))
                .isEqualTo(Intent.ACCESS_GENERAL);
    }
}
