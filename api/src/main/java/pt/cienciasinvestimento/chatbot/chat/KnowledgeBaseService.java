package pt.cienciasinvestimento.chatbot.chat;

import java.util.List;
import org.springframework.stereotype.Service;
import pt.cienciasinvestimento.chatbot.domain.Intent;

@Service
public class KnowledgeBaseService {

    private static final String AVAILABLE_TRAININGS = """
            Pelo que está visível no site da Ciências do Investimento, as principais formações/planos disponíveis ou em divulgação são:
            - Silver Member: subscrição mensal com lives de Análise Semanal, gravações, relatórios, atas de política monetária, notas de imprensa, artigos e Top Economics Classes.
            - Gold Member: mentoria pessoal mensal com sessões semanais online e formação adaptada ao nível e objetivos do cliente.
            - Trader I #FCT: formação de base, indicada no site para setembro de 2025 a maio de 2026.
            - Trader II #FCT: formação de continuação, indicada para quem já concluiu Trader I.
            - Master Mind #FCT: formação/capacitação com reuniões semanais de grupo, acesso a conteúdos Mastermind e ao conteúdo Silver Member.
            - Análise Técnica I #FCT: formação dedicada à análise técnica.
            """.trim();

    public KnowledgeBaseAnswer answerFor(Intent intent, String userMessage) {
        String normalized = TextNormalizer.normalize(userMessage);

        if (isSilverMemberQuestion(normalized)) {
            return new KnowledgeBaseAnswer(
                    "No plano Silver Member, a subscrição dá acesso às Lives de Análise Semanal em direto, às gravações das duas transmissões ao vivo mais recentes e aos respetivos relatórios de análise fundamental, a partir da data de subscrição e enquanto a subscrição estiver ativa. Também inclui Atas e Decisões de Política Monetária e Economics Classes relativas ao último ano.",
                    List.of("Tenho acesso ao conteúdo imediatamente?", "Qual é a data de vencimento da subscrição?", "Posso alterar o método de pagamento?")
            );
        }

        if (isImmediateAccessQuestion(normalized)) {
            return new KnowledgeBaseAnswer(
                    "Sim, em regra o acesso é disponibilizado após receber o email de confirmação de pagamento. O conteúdo fica na Área Reservada, nas secções de Vídeos e Artigos. Há uma exceção importante: nas formações Trader I e Mastermind, o conteúdo é libertado periodicamente.",
                    List.of("Não consigo aceder ao curso", "Que conteúdo inclui o Silver Member?", "Falar com suporte")
            );
        }

        if (isSubscriptionRenewalQuestion(normalized)) {
            return new KnowledgeBaseAnswer(
                    "A subscrição é cobrada no momento da compra e depois renovada com cobranças mensais periódicas até ser cancelada pelo cliente. Antes da renovação, deverá receber um email a confirmar a cobrança. Pode gerir a subscrição no perfil de cliente, no menu Subscrições.",
                    List.of("Posso alterar o método de pagamento?", "Quero cancelar a subscrição", "Falar com suporte")
            );
        }

        if (isMultibancoSubscriptionQuestion(normalized)) {
            return new KnowledgeBaseAnswer(
                    "Para fazer uma subscrição com pagamento por referência multibanco, deve pedir instruções por email para info@cienciasdoinvestimento.com. A equipa dará seguimento ao pedido e indicará os passos necessários.",
                    List.of("Quais os métodos de pagamento?", "Tenho acesso ao conteúdo imediatamente?", "Falar com suporte")
            );
        }

        if (isPaymentMethodChangeQuestion(normalized)) {
            return new KnowledgeBaseAnswer(
                    "A subscrição é cobrada automaticamente no método de pagamento usado na compra. Se quiser alterar esse método, pode fazê-lo no perfil de cliente, no menu Subscrições. Se houver algum erro concreto com a cobrança, o melhor é falar com suporte.",
                    List.of("Qual é a data de vencimento da subscrição?", "Falar com suporte", "Como recupero a password?")
            );
        }

        if (isVideoPlaybackQuestion(normalized)) {
            return videoPlaybackAnswer();
        }

        if (isInvoiceTaxOrIrsQuestion(normalized)) {
            return new KnowledgeBaseAnswer(
                    "Sim. As faturas são emitidas por uma entidade registada com serviços de educação. Sendo a empresa um estabelecimento integrado no Sistema Nacional de Educação e reconhecido pela DGERT, as formações beneficiam da isenção prevista na alínea 10) do artigo 9.º do Código do IVA, pelo que os serviços de formação não estão sujeitos à taxa normal de IVA de 23%.\n\nAlém disso, o valor pago pela formação pode ser considerado despesa de educação para efeitos de IRS, conforme a legislação aplicável. Em regra, as despesas de educação permitem deduzir 30% do valor gasto, até ao limite legal previsto para o agregado familiar, normalmente 800 € por ano, podendo variar em situações especiais.\n\nExemplo: numa mensalidade Silver Member de 67,50 €, a dedução estimada é 30% x 67,50 € = 20,25 € por mês. Em 3 meses, isso corresponde a uma dedução estimada de 60,75 €.",
                    List.of("Como recebo a fatura?", "As formações têm certificado?", "Posso alterar o método de pagamento?")
            );
        }

        if (intent == Intent.CERTIFICATION) {
            return certificationAnswer();
        }

        if (isAvailableTrainingsQuestion(normalized)) {
            return new KnowledgeBaseAnswer(
                    AVAILABLE_TRAININGS + "\n\nComo as datas e vagas podem mudar, recomendo confirmar sempre a página de formações/planos no site antes de avançar.",
                    List.of("Qual o melhor curso para começar?", "O que inclui o Silver Member?", "As formações têm certificado?")
            );
        }

        if (isTraderIIQuestion(normalized)) {
            return new KnowledgeBaseAnswer(
                    "O Trader II #FCT aparece no site como uma formação de continuação, com indicação de que é obrigatório ter concluído Trader I. Inclui participação na formação, lives de Análise Semanal, gravações, grupo Telegram CDI, relatórios semanais, atas de política monetária, notas de imprensa, lives/vídeos privados, artigos e Top Economics Classes.",
                    List.of("O que é o Trader I?", "As formações têm certificado?", "Falar com suporte")
            );
        }

        if (isTraderIQuestion(normalized)) {
            return new KnowledgeBaseAnswer(
                    "O Trader I #FCT é a formação de base indicada no site. O plano apresenta participação na ação de formação Trader I, acesso às gravações das aulas enquanto a subscrição estiver ativa e até 60 dias após o fim da formação, e, a partir da conclusão do módulo 4, acesso às lives de Análise Semanal, gravações dessas lives, grupo Telegram CDI, relatórios, atas de política monetária, notas de imprensa, Top Economics Classes e artigos de ensino.",
                    List.of("As formações têm certificado?", "O que é o Trader II?", "O que inclui o Silver Member?")
            );
        }

        if (isMastermindQuestion(normalized)) {
            return new KnowledgeBaseAnswer(
                    "O Master Mind #FCT é uma formação/capacitação indicada para membros que já concluíram Trader I. Inclui reunião semanal de grupo para sessão de Master Mind e Papo de Mercado, grupo Telegram exclusivo, acesso às gravações das reuniões, conteúdos de desenvolvimento de atitude, participação nas lives de Análise Semanal e acesso ao conteúdo Silver Member.",
                    List.of("Tenho acesso ao conteúdo imediatamente?", "As formações têm certificado?", "O que inclui o Silver Member?")
            );
        }

        if (isTechnicalAnalysisQuestion(normalized)) {
            return new KnowledgeBaseAnswer(
                    "A Análise Técnica I #FCT é apresentada como formação dedicada à análise técnica. O plano inclui participação na ação de formação, lives de Análise Semanal durante o período de formação, acesso às gravações das lives e acesso às gravações das aulas enquanto a subscrição estiver ativa.",
                    List.of("Que formações têm disponíveis?", "As formações têm certificado?", "Qual o melhor curso para começar?")
            );
        }

        if (intent == Intent.TRAINING_INFO && containsAny(normalized, List.of("comecar", "iniciante", "melhor curso", "primeiro curso"))) {
            return new KnowledgeBaseAnswer(
                    "Boa pergunta. Para começar, eu escolheria uma formação introdutória e progressiva, que explique primeiro os conceitos essenciais antes de entrar em temas mais específicos. Assim evita saltar etapas. O ideal é confirmar a oferta atual na página de formações e escolher a opção indicada para o seu nível de experiência.",
                    List.of("Que formações têm disponíveis?", "O que costuma estar incluído?", "A formação dá certificado?")
            );
        }

        return switch (intent) {
            case TRAINING_INFO -> new KnowledgeBaseAnswer(
                    AVAILABLE_TRAININGS + "\n\nSe quiser, posso explicar uma delas com mais detalhe.",
                    List.of("O que inclui o Silver Member?", "O que é o Trader I?", "As formações têm certificado?")
            );
            case PAYMENT_GENERAL -> new KnowledgeBaseAnswer(
                    "Posso ajudar com a parte geral. Os pagamentos são feitos através dos métodos apresentados no checkout e, depois da confirmação, o acesso costuma ser ativado automaticamente. Se estiver a falar de um pagamento seu em concreto, aí é melhor tratar com suporte para proteger os seus dados.",
                    List.of("Paguei mas ainda não tenho acesso", "Como recebo o comprovativo?", "Quais os métodos de pagamento?")
            );
            case ACCESS_GENERAL -> new KnowledgeBaseAnswer(
                    "Vamos por partes. Para aceder ao site, entre na área de cliente com o email usado no registo ou na compra. Se não estiver a conseguir entrar, confirme primeiro o email, tente recuperar a password e verifique se está a usar um browser atualizado.",
                    List.of("O vídeo aparece como privado", "Como recupero a password?", "Falar com suporte")
            );
            case PASSWORD_RECOVERY -> new KnowledgeBaseAnswer(
                    "Sim. Vá à página de login e escolha a opção para recuperar ou repor a password. Depois introduza o email associado à conta e siga as instruções enviadas por email. Se não encontrar a mensagem, vale a pena ver a pasta de spam, promoções ou lixo eletrónico.",
                    List.of("Não recebi o email", "Continuo sem conseguir entrar", "Falar com suporte")
            );
            case SUBSCRIPTION_GENERAL -> new KnowledgeBaseAnswer(
                    "As subscrições dependem do plano contratado e das condições apresentadas no momento da compra. Para uma dúvida geral, consulte a área de cliente e os termos da subscrição. Se a pergunta for sobre a sua subscrição em concreto, encaminho para suporte para que possam verificar com segurança.",
                    List.of("Como vejo a minha subscrição?", "Quero cancelar a subscrição", "Falar com suporte")
            );
            case CERTIFICATION -> certificationAnswer();
            case FINANCIAL_ADVICE_BLOCKED -> new KnowledgeBaseAnswer(
                    "Não posso dar aconselhamento financeiro, recomendações de investimento ou indicações de compra/venda de ativos. Posso, no entanto, ajudar a encontrar conteúdos educativos ou explicar que formações existem para aprender o tema com mais estrutura.",
                    List.of("Que formações têm disponíveis?", "Qual o melhor curso para começar?", "Falar com suporte")
            );
            case HUMAN_SUPPORT, PERSONAL_ACCOUNT_ISSUE -> new KnowledgeBaseAnswer(
                    "Isto já parece envolver a sua conta, pagamento ou acesso individual. Para proteger os seus dados, o melhor é passar esta situação para suporte humano.",
                    List.of("Contactar suporte", "Como recupero a password?", "Ver ajuda de acesso")
            );
            case UNKNOWN -> new KnowledgeBaseAnswer(
                    "Não quero inventar uma resposta. Posso ajudar com dúvidas gerais sobre formações, pagamentos, subscrições, acesso ao site e recuperação de password. Se me der um pouco mais de contexto, tento orientar melhor.",
                    List.of("Que formações têm disponíveis?", "Como recupero a password?", "Falar com suporte")
            );
        };
    }

    private boolean containsAny(String value, List<String> terms) {
        return terms.stream().anyMatch(value::contains);
    }

    private KnowledgeBaseAnswer certificationAnswer() {
        return new KnowledgeBaseAnswer(
                "Sim. Todas as formações da Ciências do Investimento são certificadas pela DGERT, a Direção-Geral do Emprego e das Relações de Trabalho. No site, as formações certificadas indicam também vantagens como certificado com reconhecimento oficial, processos credibilizados por entidade reguladora e possibilidade de dedução de despesas de educação em IRS, quando aplicável.",
                List.of("Que formações têm disponíveis?", "Quais os requisitos do certificado?", "Falar com suporte")
        );
    }

    private KnowledgeBaseAnswer videoPlaybackAnswer() {
        return new KnowledgeBaseAnswer(
                "Vamos resolver por passos. A sua subscrição está ativa?",
                List.of("Sim, está ativa", "Não, não está ativa", "Não sei")
        );
    }

    private boolean isSilverMemberQuestion(String normalized) {
        return containsAny(normalized, List.of(
                "silver member",
                "lives de analise semanal",
                "analise semanal",
                "atas e decisoes",
                "politica monetaria",
                "economics classes"
        ));
    }

    private boolean isAvailableTrainingsQuestion(String normalized) {
        return containsAny(normalized, List.of(
                "que formacoes",
                "formacoes disponiveis",
                "formacoes tem",
                "cursos disponiveis",
                "cursos tem",
                "em andamento",
                "a decorrer"
        ));
    }

    private boolean isTraderIQuestion(String normalized) {
        return containsAny(normalized, List.of("trader i", "trader nivel i", "trader 1"));
    }

    private boolean isTraderIIQuestion(String normalized) {
        return containsAny(normalized, List.of("trader ii", "trader nivel ii", "trader 2"));
    }

    private boolean isMastermindQuestion(String normalized) {
        return containsAny(normalized, List.of("mastermind", "master mind"));
    }

    private boolean isTechnicalAnalysisQuestion(String normalized) {
        return containsAny(normalized, List.of("analise tecnica", "analise tecnica i"));
    }

    private boolean isImmediateAccessQuestion(String normalized) {
        return containsAny(normalized, List.of(
                "acesso imediatamente",
                "acesso imediato",
                "conteudo imediatamente",
                "quando tenho acesso",
                "quando fica disponivel",
                "quando e libertado",
                "area reservada videos",
                "area reservada artigos"
        ));
    }

    private boolean isSubscriptionRenewalQuestion(String normalized) {
        return containsAny(normalized, List.of(
                "data de vencimento",
                "vencimento da minha subscricao",
                "renovacao da subscricao",
                "quando e cobrada",
                "cobrancas mensais",
                "cobranca mensal"
        ));
    }

    private boolean isMultibancoSubscriptionQuestion(String normalized) {
        return containsAny(normalized, List.of(
                "referencia multibanco",
                "pagamento por multibanco",
                "pagar por multibanco",
                "subscricao por multibanco"
        ));
    }

    private boolean isPaymentMethodChangeQuestion(String normalized) {
        return containsAny(normalized, List.of(
                "alterar metodo de pagamento",
                "mudar metodo de pagamento",
                "trocar metodo de pagamento",
                "alterar o cartao",
                "mudar cartao",
                "metodo de pagamento"
        ));
    }

    private boolean isVideoPlaybackQuestion(String normalized) {
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

    private boolean isInvoiceTaxOrIrsQuestion(String normalized) {
        return containsAny(normalized, List.of(
                "irs",
                "iva",
                "fatura",
                "faturacao",
                "factura",
                "recibo",
                "deducao",
                "deduzir",
                "despesa de educacao",
                "despesas de educacao",
                "artigo 9",
                "civa"
        ));
    }
}
