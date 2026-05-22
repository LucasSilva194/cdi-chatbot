package pt.cienciasinvestimento.chatbot.chat;

import java.util.List;
import org.springframework.stereotype.Service;
import pt.cienciasinvestimento.chatbot.domain.Intent;

@Service
public class IntentClassifierService {

    public Intent classify(String message) {
        String normalized = TextNormalizer.normalize(message);

        if (looksLikeFinancialAdvice(normalized)) {
            return Intent.FINANCIAL_ADVICE_BLOCKED;
        }

        if (containsAny(normalized, List.of("falar com suporte", "suporte humano", "falar com uma pessoa", "contactar suporte"))) {
            return Intent.HUMAN_SUPPORT;
        }

        if (looksLikePersonalAccountIssue(normalized)) {
            return Intent.PERSONAL_ACCOUNT_ISSUE;
        }

        if (containsAny(normalized, List.of("password", "palavra passe", "recuperar passe", "esqueci", "repor password", "reset password"))) {
            return Intent.PASSWORD_RECOVERY;
        }

        if (containsAny(normalized, List.of("certificado", "certificacao", "diploma", "comprovativo"))) {
            return Intent.CERTIFICATION;
        }

        if (containsAny(normalized, List.of("subscricao", "assinatura", "mensalidade", "plano", "renovacao", "cancelar subscricao"))) {
            return Intent.SUBSCRIPTION_GENERAL;
        }

        if (containsAny(normalized, List.of("pagamento", "pagar", "cartao", "mb way", "multibanco", "paypal", "fatura", "recibo", "preco"))) {
            return Intent.PAYMENT_GENERAL;
        }

        if (containsAny(normalized, List.of("aceder ao site", "entrar no site", "login", "iniciar sessao", "area de cliente", "plataforma"))) {
            return Intent.ACCESS_GENERAL;
        }

        if (containsAny(normalized, List.of("formacao", "formacoes", "curso", "cursos", "conteudos", "modulos", "aulas", "comecar", "iniciante"))) {
            return Intent.TRAINING_INFO;
        }

        return Intent.UNKNOWN;
    }

    private boolean looksLikeFinancialAdvice(String normalized) {
        return containsAny(normalized, List.of("devo investir", "devo comprar", "devo vender", "vale a pena investir"))
                || (containsAny(normalized, List.of("acao", "acoes", "etf", "cripto", "fundo", "fundos", "ativo", "ativos"))
                && containsAny(normalized, List.of("comprar", "vender", "investir", "melhor", "recomendas", "aconselhas")));
    }

    private boolean looksLikePersonalAccountIssue(String normalized) {
        boolean personalPayment = containsAny(normalized, List.of(
                "paguei",
                "o meu pagamento",
                "minha encomenda",
                "minha compra",
                "comprei",
                "cobraram",
                "foi debitado",
                "reembolso",
                "falhou o pagamento"
        ));

        boolean individualAccess = containsAny(normalized, List.of(
                "nao consigo aceder ao curso",
                "nao tenho acesso",
                "perdi acesso",
                "o meu acesso",
                "minha conta",
                "a minha conta",
                "bloqueada",
                "email da compra"
        ));

        return personalPayment || individualAccess;
    }

    private boolean containsAny(String value, List<String> terms) {
        return terms.stream().anyMatch(value::contains);
    }
}
