package pt.cienciasinvestimento.chatbot.chat;

import java.util.List;
import org.springframework.stereotype.Service;
import pt.cienciasinvestimento.chatbot.domain.Intent;

@Service
public class EscalationService {

    public boolean requiresEscalation(Intent intent, String message) {
        if (intent == Intent.PERSONAL_ACCOUNT_ISSUE || intent == Intent.HUMAN_SUPPORT) {
            return true;
        }

        String normalized = TextNormalizer.normalize(message);
        return containsAny(normalized, List.of(
                "nif",
                "morada",
                "telemovel",
                "telefone",
                "cartao",
                "iban",
                "encomenda",
                "fatura da minha",
                "recibo da minha",
                "email da compra"
        ));
    }

    public String reasonFor(Intent intent) {
        if (intent == Intent.HUMAN_SUPPORT) {
            return "O utilizador pediu contacto com suporte humano.";
        }

        return "A questão pode envolver dados pessoais, pagamento específico, compra ou acesso individual.";
    }

    public KnowledgeBaseAnswer escalationAnswer(Intent intent) {
        String message = """
                Esta situação pode envolver dados da sua conta, pagamento, compra ou acesso individual.
                Para proteger os seus dados, vou encaminhar para suporte humano. Por favor contacte suporte@cienciasdoinvestimento.pt e indique o email usado na compra e a formação em causa, sem partilhar dados sensíveis neste chat.
                """.trim();

        if (intent == Intent.HUMAN_SUPPORT) {
            message = "Claro. Para falar com suporte humano, contacte suporte@cienciasdoinvestimento.pt e descreva a sua questão de forma resumida.";
        }

        return new KnowledgeBaseAnswer(
                message,
                List.of("Como recupero a password?", "Que formações têm disponíveis?", "Voltar ao início")
        );
    }

    private boolean containsAny(String value, List<String> terms) {
        return terms.stream().anyMatch(value::contains);
    }
}
