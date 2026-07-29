import type { ChatRequest, ChatResponse, Intent } from '../types/chat';

type VideoStep =
  | 'ASK_ACTIVE_SUBSCRIPTION'
  | 'ASK_DEVICE'
  | 'ASK_YOUTUBE_LOGIN'
  | 'ASK_BROWSER';

const videoFlows = new Map<string, VideoStep>();

export async function sendDemoChatMessage(request: ChatRequest): Promise<ChatResponse> {
  const conversationId = request.conversationId || createConversationId();
  const message = request.message.trim();
  const normalized = normalize(message);

  const videoResponse = handleVideoFlow(conversationId, normalized);
  if (videoResponse) {
    return withConversation(conversationId, videoResponse);
  }

  if (isVideoIssue(normalized)) {
    videoFlows.set(conversationId, 'ASK_ACTIVE_SUBSCRIPTION');
    return withConversation(conversationId, {
      intent: 'ACCESS_GENERAL',
      message: 'Vamos ver isso passo a passo. Tem uma subscrição ativa para essa formação?',
      escalated: false,
      escalationReason: null,
      suggestions: ['Sim, tenho subscrição ativa', 'Não tenho subscrição ativa', 'Não tenho a certeza'],
    });
  }

  if (isFinancialAdvice(normalized)) {
    return withConversation(conversationId, {
      intent: 'FINANCIAL_ADVICE_BLOCKED',
      message: 'Não posso dar aconselhamento financeiro nem recomendar compra ou venda de ativos. Posso ajudar a encontrar conteúdos educativos para aprender sobre ETFs, ações, risco e construção de conhecimento.',
      escalated: false,
      escalationReason: null,
      suggestions: ['Que formações têm disponíveis?', 'Qual o melhor curso para começar?', 'Falar com suporte'],
    });
  }

  if (isPersonalIssue(normalized)) {
    return withConversation(conversationId, {
      intent: 'PERSONAL_ACCOUNT_ISSUE',
      message: 'Para questões sobre pagamentos, compras ou acessos específicos da sua conta, o melhor é falar com suporte humano. Assim conseguimos confirmar a situação sem expor dados sensíveis no chat.',
      escalated: true,
      escalationReason: 'Questão sobre conta, compra, pagamento ou acesso individual.',
      suggestions: ['Falar com suporte', 'Como acedo ao site?', 'Que formações têm disponíveis?'],
    });
  }

  const answer = answerFaq(normalized);
  return withConversation(conversationId, answer);
}

function handleVideoFlow(conversationId: string, normalized: string): Omit<ChatResponse, 'conversationId'> | null {
  const step = videoFlows.get(conversationId);
  if (!step) {
    return null;
  }

  if (step === 'ASK_ACTIVE_SUBSCRIPTION') {
    if (meansNo(normalized) || normalized.includes('nao tenho subscricao')) {
      videoFlows.delete(conversationId);
      return {
        intent: 'ACCESS_GENERAL',
        message: 'Se a subscrição não estiver ativa, o vídeo pode aparecer como privado. Confirme a subscrição na área de cliente ou fale com suporte para verificarem o acesso.',
        escalated: true,
        escalationReason: 'Possível falta de subscrição ativa.',
        suggestions: ['Falar com suporte', 'Como vejo a minha subscrição?', 'Que formações têm disponíveis?'],
      };
    }

    if (meansYes(normalized)) {
      videoFlows.set(conversationId, 'ASK_DEVICE');
      return {
        intent: 'ACCESS_GENERAL',
        message: 'Perfeito. Está a tentar ver os vídeos no telemóvel ou no computador?',
        escalated: false,
        escalationReason: null,
        suggestions: ['Telemóvel', 'Computador'],
      };
    }

    return {
      intent: 'ACCESS_GENERAL',
      message: 'Sem problema. Primeiro precisamos de confirmar se a subscrição está ativa, porque isso pode explicar o vídeo aparecer como privado.',
      escalated: true,
      escalationReason: 'Subscrição por confirmar.',
      suggestions: ['Falar com suporte', 'Sim, tenho subscrição ativa', 'Não tenho subscrição ativa'],
    };
  }

  if (step === 'ASK_DEVICE') {
    if (containsAny(normalized, ['telemovel', 'mobile', 'iphone', 'android'])) {
      videoFlows.delete(conversationId);
      return {
        intent: 'ACCESS_GENERAL',
        message: 'No telemóvel, abra o site da CDI no browser e inicie sessão no YouTube nesse mesmo browser. Não basta estar autenticado na aplicação do YouTube. Depois volte à formação e atualize a página.',
        escalated: false,
        escalationReason: null,
        suggestions: ['Continuo sem conseguir ver', 'Falar com suporte', 'Estou no computador'],
      };
    }

    if (containsAny(normalized, ['computador', 'pc', 'desktop', 'portatil'])) {
      videoFlows.set(conversationId, 'ASK_YOUTUBE_LOGIN');
      return {
        intent: 'ACCESS_GENERAL',
        message: 'No computador, confirme primeiro se tem sessão iniciada no YouTube no mesmo browser onde abriu o site da CDI. Tem sessão iniciada?',
        escalated: false,
        escalationReason: null,
        suggestions: ['Sim, tenho sessão no YouTube', 'Não tenho sessão no YouTube'],
      };
    }
  }

  if (step === 'ASK_YOUTUBE_LOGIN') {
    if (meansNo(normalized)) {
      videoFlows.delete(conversationId);
      return {
        intent: 'ACCESS_GENERAL',
        message: 'Esse pode ser o problema. Inicie sessão no YouTube no mesmo browser onde está a usar o site da CDI, volte à formação e atualize a página.',
        escalated: false,
        escalationReason: null,
        suggestions: ['Continuo sem conseguir ver', 'Falar com suporte', 'Que browser devo usar?'],
      };
    }

    if (meansYes(normalized)) {
      videoFlows.set(conversationId, 'ASK_BROWSER');
      return {
        intent: 'ACCESS_GENERAL',
        message: 'Obrigado. Qual é o browser que está a usar?',
        escalated: false,
        escalationReason: null,
        suggestions: ['Chrome', 'Edge', 'Firefox', 'Brave'],
      };
    }
  }

  if (step === 'ASK_BROWSER') {
    const browser = detectBrowser(normalized);
    if (browser) {
      videoFlows.delete(conversationId);
      return browserInstructions(browser);
    }
  }

  return {
    intent: 'ACCESS_GENERAL',
    message: 'Não consegui perceber esse passo. Pode escolher uma das opções abaixo para continuarmos?',
    escalated: false,
    escalationReason: null,
    suggestions: suggestionsForStep(step),
  };
}

function answerFaq(normalized: string): Omit<ChatResponse, 'conversationId'> {
  if (containsAny(normalized, ['certificado', 'certificada', 'dgert'])) {
    return {
      intent: 'CERTIFICATION',
      message: 'Sim. As formações são certificadas pela DGERT, a Direção-Geral do Emprego e das Relações de Trabalho.',
      escalated: false,
      escalationReason: null,
      suggestions: ['A formação entra no IRS?', 'Que formações têm disponíveis?', 'Como recebo a fatura?'],
    };
  }

  if (containsAny(normalized, ['irs', 'fatura', 'faturacao', 'iva', 'deduzir', 'deducao'])) {
    return {
      intent: 'PAYMENT_GENERAL',
      message: 'As formações são emitidas por uma entidade registada com serviços de educação e beneficiam de isenção de IVA. Para IRS, o valor pode ser considerado despesa de educação, com dedução de 30% dentro dos limites legais aplicáveis.',
      escalated: false,
      escalationReason: null,
      suggestions: ['Como recebo a fatura?', 'Quais os métodos de pagamento?', 'Falar com suporte'],
    };
  }

  if (containsAny(normalized, ['pagamento', 'metodos', 'cartao', 'mbway', 'multibanco', 'pagar'])) {
    return {
      intent: 'PAYMENT_GENERAL',
      message: 'Os métodos de pagamento podem variar, mas normalmente o pagamento é feito online e as parcelas de planos são cobradas automaticamente no cartão associado. A fatura é enviada para o email usado na compra.',
      escalated: false,
      escalationReason: null,
      suggestions: ['Paguei mas ainda não tenho acesso', 'Como recebo a fatura?', 'A formação entra no IRS?'],
    };
  }

  if (containsAny(normalized, ['codigo', 'email de login', 'login', 'entrar', 'aceder ao site'])) {
    return {
      intent: 'ACCESS_GENERAL',
      message: 'O acesso ao site é feito por código enviado por email. Aceda à página de login, introduza o email usado na compra e confirme o código recebido. Verifique também spam e promoções.',
      escalated: false,
      escalationReason: null,
      suggestions: ['Não recebi o código', 'Continuo sem conseguir entrar', 'Falar com suporte'],
    };
  }

  if (containsAny(normalized, ['subscricao', 'subscrições', 'cancelar', 'renovacao'])) {
    return {
      intent: 'SUBSCRIPTION_GENERAL',
      message: 'As subscrições podem ser geridas na área de cliente. Para cancelar, deve fazê-lo antes do próximo vencimento. Se for um caso específico da sua conta, fale com suporte.',
      escalated: false,
      escalationReason: null,
      suggestions: ['Como vejo a minha subscrição?', 'Quero cancelar a subscrição', 'Falar com suporte'],
    };
  }

  if (containsAny(normalized, ['formacoes', 'formacao', 'cursos', 'curso', 'disponiveis', 'mastermind', 'trader', 'silver'])) {
    return {
      intent: 'TRAINING_INFO',
      message: 'As principais formações da Ciências do Investimento incluem Trader I, Silver Member e Mastermind. São conteúdos educativos sobre literacia financeira, investimento e mercados. Para começar, normalmente o Trader I é o ponto mais adequado.',
      escalated: false,
      escalationReason: null,
      suggestions: ['Qual o melhor curso para começar?', 'A formação dá certificado?', 'Quais os métodos de pagamento?'],
    };
  }

  return {
    intent: 'UNKNOWN',
    message: 'Posso ajudar com formações, pagamentos gerais, certificados, IRS, acesso ao site e problemas técnicos com vídeos. Sobre dados da sua conta ou compras específicas, encaminho para suporte.',
    escalated: false,
    escalationReason: null,
    suggestions: ['Que formações têm disponíveis?', 'Não consigo ver os vídeos', 'Falar com suporte'],
  };
}

function browserInstructions(browser: 'chrome' | 'edge' | 'firefox' | 'brave'): Omit<ChatResponse, 'conversationId'> {
  const common = 'Depois de alterar, feche e volte a abrir a página da formação.';

  const instructions = {
    chrome: `No Chrome, vá a Definições > Privacidade e segurança > Cookies de terceiros. Permita cookies de terceiros ou adicione exceção para o site da CDI e YouTube. ${common}`,
    edge: `No Edge, vá a Definições > Cookies e permissões de site > Gerir e eliminar cookies. Permita cookies de terceiros ou adicione exceção para CDI e YouTube. ${common}`,
    firefox: `No Firefox, clique no escudo junto ao endereço do site e desative a proteção contra seguimento para o site da CDI, ou ajuste em Privacidade e Segurança. ${common}`,
    brave: `No Brave, clique no ícone Brave Shields na barra de endereço e permita cookies/seguimento para o site da CDI. ${common}`,
  };

  return {
    intent: 'ACCESS_GENERAL',
    message: instructions[browser],
    escalated: false,
    escalationReason: null,
    suggestions: ['Continuo sem conseguir ver', 'Falar com suporte', 'Tenho outro problema'],
  };
}

function isVideoIssue(normalized: string) {
  return containsAny(normalized, ['video', 'videos', 'privado', 'youtube', 'trader i', 'mastermind'])
    && containsAny(normalized, ['nao consigo', 'nao vejo', 'ver', 'aparece', 'privado', 'bloqueado']);
}

function isFinancialAdvice(normalized: string) {
  return containsAny(normalized, ['etf', 'acoes', 'acao', 'cripto', 'bitcoin', 'fundo', 'ativo', 'investir'])
    && containsAny(normalized, ['devo', 'comprar', 'vender', 'melhor', 'recomendas', 'aconselhas']);
}

function isPersonalIssue(normalized: string) {
  return containsAny(normalized, [
    'paguei mas',
    'minha compra',
    'meu pagamento',
    'nao tenho acesso',
    'continuo sem conseguir',
    'nao recebi o codigo',
    'falar com suporte',
  ]);
}

function detectBrowser(normalized: string) {
  if (normalized.includes('chrome')) return 'chrome';
  if (normalized.includes('edge')) return 'edge';
  if (normalized.includes('firefox')) return 'firefox';
  if (normalized.includes('brave')) return 'brave';
  return null;
}

function suggestionsForStep(step: VideoStep) {
  if (step === 'ASK_ACTIVE_SUBSCRIPTION') return ['Sim, tenho subscrição ativa', 'Não tenho subscrição ativa'];
  if (step === 'ASK_DEVICE') return ['Telemóvel', 'Computador'];
  if (step === 'ASK_YOUTUBE_LOGIN') return ['Sim, tenho sessão no YouTube', 'Não tenho sessão no YouTube'];
  return ['Chrome', 'Edge', 'Firefox', 'Brave'];
}

function meansYes(normalized: string) {
  return containsAny(normalized, ['sim', 'tenho', 'ativa', 'iniciada']);
}

function meansNo(normalized: string) {
  return containsAny(normalized, ['nao', 'não', 'sem', 'nao tenho']);
}

function containsAny(value: string, terms: string[]) {
  return terms.some((term) => value.includes(term));
}

function normalize(value: string) {
  return value
    .toLowerCase()
    .normalize('NFD')
    .replace(/\p{Diacritic}/gu, '')
    .trim();
}

function withConversation(conversationId: string, response: Omit<ChatResponse, 'conversationId'>): ChatResponse {
  return {
    conversationId,
    ...response,
  };
}

function createConversationId() {
  return `demo-${Math.random().toString(36).slice(2)}`;
}
