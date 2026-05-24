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

        if (looksLikeVideoPlaybackIssue(normalized)) {
            return Intent.ACCESS_GENERAL;
        }

        if (looksLikePersonalAccountIssue(normalized)) {
            return Intent.PERSONAL_ACCOUNT_ISSUE;
        }

        if (containsAny(normalized, List.of("certificado", "certificacao", "diploma", "comprovativo", "dgert"))) {
            return Intent.CERTIFICATION;
        }

        if (containsAny(normalized, List.of(
                "subscricao",
                "assinatura",
                "mensalidade",
                "plano",
                "renovacao",
                "cancelar subscricao",
                "silver member",
                "gold member",
                "mastermind",
                "master mind",
                "lives",
                "analise semanal",
                "atas",
                "politica monetaria",
                "economics classes"
        ))) {
            return Intent.SUBSCRIPTION_GENERAL;
        }

        if (containsAny(normalized, List.of(
                "pagamento",
                "pagar",
                "cartao",
                "mb way",
                "multibanco",
                "paypal",
                "fatura",
                "faturacao",
                "factura",
                "recibo",
                "preco",
                "irs",
                "iva",
                "deducao",
                "deduzir",
                "despesa de educacao",
                "despesas de educacao"
        ))) {
            return Intent.PAYMENT_GENERAL;
        }

        if (containsAny(normalized, List.of(
                "aceder ao site",
                "entrar no site",
                "login",
                "iniciar sessao",
                "area de cliente",
                "plataforma",
                "acesso imediato",
                "acesso ao conteudo",
                "video privado",
                "videos privados",
                "youtube",
                "cookies",
                "seguimento entre sites",
                "tracking entre sites",
                "codigo",
                "codigo de acesso",
                "codigo por email",
                "codigo enviado por email",
                "nao recebi o codigo",
                "email de login",
                "login por email",
                "entrar"
        ))) {
            return Intent.ACCESS_GENERAL;
        }

        if (containsAny(normalized, List.of(
                "formacao",
                "formacoes",
                "curso",
                "cursos",
                "conteudo",
                "conteudos",
                "modulos",
                "aulas",
                "comecar",
                "iniciante",
                "trader i",
                "trader ii",
                "analise tecnica",
                "analise fundamental"
        ))) {
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

    private boolean looksLikeVideoPlaybackIssue(String normalized) {
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

    private boolean containsAny(String value, List<String> terms) {
        return terms.stream().anyMatch(value::contains);
    }
}
