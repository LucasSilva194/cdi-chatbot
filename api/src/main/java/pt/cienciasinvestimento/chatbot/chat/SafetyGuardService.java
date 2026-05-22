package pt.cienciasinvestimento.chatbot.chat;

import java.util.List;
import org.springframework.stereotype.Service;
import pt.cienciasinvestimento.chatbot.domain.Intent;

@Service
public class SafetyGuardService {

    private static final String BLOCKED_MESSAGE = """
            Não posso dar aconselhamento financeiro, recomendar investimentos ou indicar compra/venda de ativos.
            Posso, no entanto, ajudar com informação educativa sobre as formações da Ciências do Investimento ou encaminhar para suporte.
            """.trim();

    private static final List<String> DIRECT_ADVICE_PHRASES = List.of(
            "devo investir",
            "devo comprar",
            "devo vender",
            "comprar ou vender",
            "vale a pena investir",
            "recomendas investir",
            "aconselhas investir",
            "que ativo compro",
            "que acao compro",
            "que etf compro",
            "preco alvo"
    );

    private static final List<String> ASSET_TERMS = List.of(
            "acao",
            "acoes",
            "etf",
            "cripto",
            "bitcoin",
            "ethereum",
            "fundo",
            "fundos",
            "obrigacao",
            "obrigacoes",
            "ativo",
            "ativos"
    );

    private static final List<String> DECISION_TERMS = List.of(
            "comprar",
            "vender",
            "investir",
            "reforcar",
            "resgatar",
            "manter",
            "melhor",
            "recomendas",
            "aconselhas"
    );

    public SafetyAssessment assess(String message) {
        String normalized = TextNormalizer.normalize(message);

        if (containsAny(normalized, DIRECT_ADVICE_PHRASES) || mentionsAssetDecision(normalized)) {
            return SafetyAssessment.blocked(Intent.FINANCIAL_ADVICE_BLOCKED, BLOCKED_MESSAGE);
        }

        return SafetyAssessment.allowed();
    }

    private boolean mentionsAssetDecision(String normalized) {
        return containsAny(normalized, ASSET_TERMS) && containsAny(normalized, DECISION_TERMS);
    }

    private boolean containsAny(String value, List<String> terms) {
        return terms.stream().anyMatch(value::contains);
    }
}
