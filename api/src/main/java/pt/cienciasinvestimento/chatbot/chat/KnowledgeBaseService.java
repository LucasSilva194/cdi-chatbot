package pt.cienciasinvestimento.chatbot.chat;

import java.util.List;
import org.springframework.stereotype.Service;
import pt.cienciasinvestimento.chatbot.domain.Intent;

@Service
public class KnowledgeBaseService {

    public KnowledgeBaseAnswer answerFor(Intent intent, String userMessage) {
        String normalized = TextNormalizer.normalize(userMessage);

        if (intent == Intent.TRAINING_INFO && containsAny(normalized, List.of("comecar", "iniciante", "melhor curso", "primeiro curso"))) {
            return new KnowledgeBaseAnswer(
                    "Para começar, o mais indicado é escolher uma formação introdutória e progressiva, focada em conceitos essenciais antes de avançar para temas mais específicos. Consulte a página de formações da Ciências do Investimento para ver as opções atuais e escolha a que melhor corresponde ao seu nível de experiência.",
                    List.of("Que formações têm disponíveis?", "Quais os conteúdos incluídos?", "A formação dá certificado?")
            );
        }

        return switch (intent) {
            case TRAINING_INFO -> new KnowledgeBaseAnswer(
                    "As formações disponíveis podem variar ao longo do tempo. Em geral, a Ciências do Investimento disponibiliza conteúdos educativos sobre literacia financeira, investimento e análise de mercados, organizados por módulos e aulas. Para ver a oferta atual, consulte a página de formações no site.",
                    List.of("Quais os conteúdos incluídos?", "Qual o melhor curso para começar?", "A formação dá certificado?")
            );
            case PAYMENT_GENERAL -> new KnowledgeBaseAnswer(
                    "Os pagamentos são feitos através dos métodos apresentados no checkout. Depois da confirmação do pagamento, o acesso é normalmente ativado de forma automática. Se a questão for sobre um pagamento específico, devo encaminhar para suporte para proteger os seus dados.",
                    List.of("Paguei mas ainda não tenho acesso", "Como recebo o comprovativo?", "Quais os métodos de pagamento?")
            );
            case ACCESS_GENERAL -> new KnowledgeBaseAnswer(
                    "Para aceder ao site, entre na área de cliente com o email usado no registo ou na compra. Se tiver dificuldades gerais, confirme se está a usar o email correto, tente recuperar a password e verifique se o browser está atualizado.",
                    List.of("Como recupero a password?", "Não consigo aceder ao curso", "Falar com suporte")
            );
            case PASSWORD_RECOVERY -> new KnowledgeBaseAnswer(
                    "Para recuperar a password, aceda à página de login e escolha a opção de recuperação de password. Introduza o email associado à sua conta e siga as instruções enviadas por email. Verifique também a pasta de spam ou promoções.",
                    List.of("Não recebi o email", "Continuo sem conseguir entrar", "Falar com suporte")
            );
            case SUBSCRIPTION_GENERAL -> new KnowledgeBaseAnswer(
                    "As subscrições dependem do plano contratado e das condições apresentadas no momento da compra. Para dúvidas gerais, consulte a área de cliente e os termos da subscrição. Questões sobre uma subscrição concreta devem ser tratadas pelo suporte.",
                    List.of("Como vejo a minha subscrição?", "Quero cancelar a subscrição", "Falar com suporte")
            );
            case CERTIFICATION -> new KnowledgeBaseAnswer(
                    "Quando uma formação inclui certificado, essa informação é indicada na página da própria formação. Em regra, o certificado ou comprovativo fica disponível após cumprir os requisitos definidos para essa formação.",
                    List.of("Que formações têm disponíveis?", "Quais os requisitos do certificado?", "Falar com suporte")
            );
            case FINANCIAL_ADVICE_BLOCKED -> new KnowledgeBaseAnswer(
                    "Não posso dar aconselhamento financeiro, recomendações de investimento ou indicações de compra/venda de ativos. Posso ajudar com informação sobre as formações da Ciências do Investimento ou com dúvidas de suporte.",
                    List.of("Que formações têm disponíveis?", "Qual o melhor curso para começar?", "Falar com suporte")
            );
            case HUMAN_SUPPORT, PERSONAL_ACCOUNT_ISSUE -> new KnowledgeBaseAnswer(
                    "Esta situação pode envolver dados da sua conta, pagamento ou acesso individual. Para proteger os seus dados, deve ser tratada por suporte humano.",
                    List.of("Contactar suporte", "Como recupero a password?", "Ver ajuda de acesso")
            );
            case UNKNOWN -> new KnowledgeBaseAnswer(
                    "Ainda não tenho uma resposta segura para essa questão. Posso ajudar com dúvidas gerais sobre formações, pagamentos, subscrições, acesso ao site e recuperação de password.",
                    List.of("Que formações têm disponíveis?", "Como recupero a password?", "Falar com suporte")
            );
        };
    }

    private boolean containsAny(String value, List<String> terms) {
        return terms.stream().anyMatch(value::contains);
    }
}
