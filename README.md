# CDI Chatbot

Chatbot de suporte ao cliente para a Ciencias do Investimento, dividido em duas aplicacoes:

- `ui`: Vue 3 + TypeScript + Vite
- `api`: Java 21 + Spring Boot + PostgreSQL + Redis opcional para estado de conversa

A Fase 1 responde a perguntas frequentes sem consultar dados pessoais. Perguntas sobre conta, pagamento individual, compras especificas ou acesso individual sao encaminhadas para suporte humano. Pedidos de aconselhamento financeiro sao bloqueados de forma educada.

## Arquitetura

```text
UI Vue
  -> Spring Boot API
    -> FAQ/RAG Service local
    -> PostgreSQL
    -> Redis para estado temporario de fluxos conversacionais
    -> Shopify Service futuro
    -> LLM Service futuro
```

## Requisitos

- Java 21
- Node.js 20 ou superior
- Docker e Docker Compose

## Base de dados

```bash
docker compose up -d postgres
```

Opcionalmente, pode iniciar Redis. Em producao, Redis deve ser usado para guardar o estado temporario dos fluxos conversacionais:

```bash
docker compose --profile redis up -d
```

## API

```bash
cd api
cp .env.example .env
./mvnw spring-boot:run
```

A API fica disponivel em `http://localhost:8080`.

Por defeito, o PostgreSQL do Docker fica exposto em `localhost:5433` para evitar conflito com instalacoes locais que usem `5432`.

### Estado de conversa

O `conversationId` e enviado pela UI em cada mensagem. A API usa esse id para guardar o passo atual de fluxos guiados, por exemplo o troubleshooting de videos privados.

Em desenvolvimento, o estado usa memoria local com TTL:

```env
APP_CONVERSATION_STATE_STORE=memory
APP_CONVERSATION_STATE_TTL=PT2H
```

Em producao, use Redis para que o estado sobreviva a multiplas instancias da API e expire automaticamente:

```env
APP_CONVERSATION_STATE_STORE=redis
APP_CONVERSATION_STATE_TTL=PT2H
SPRING_DATA_REDIS_HOST=redis-host
SPRING_DATA_REDIS_PORT=6379
SPRING_DATA_REDIS_PASSWORD=secret
SPRING_DATA_REDIS_SSL_ENABLED=true
```

O estado de conversa e temporario e nao deve conter dados sensiveis. Logs basicos continuam a ser gravados em PostgreSQL.

### Testes da API

```bash
cd api
./mvnw test
```

## UI

```bash
cd ui
cp .env.example .env
npm install
npm run dev
```

A UI fica disponivel em `http://localhost:5173`.

Por defeito, a UI monta o chatbot como widget flutuante, pensado para ser embutido no site. O componente principal e `ChatWidget.vue`:

```vue
<ChatWidget title="Genius" />
```

Para testes internos, o mesmo componente pode ser usado em modo inline com diagnosticos:

```vue
<ChatWidget title="Painel de teste" :floating="false" :initially-open="true" :show-diagnostics="true" />
```

## Endpoint principal

`POST /api/chat`

```json
{
  "conversationId": "demo-1",
  "message": "Que formacoes tem disponiveis?"
}
```

Resposta:

```json
{
  "conversationId": "demo-1",
  "intent": "TRAINING_INFO",
  "message": "Temos formacoes orientadas para literacia financeira...",
  "escalated": false,
  "escalationReason": null,
  "suggestions": ["Quais os conteudos incluidos?", "A formacao da certificado?"]
}
```

Mais exemplos estao em `requests/chat.http`.

## Intents implementadas

- `TRAINING_INFO`
- `PAYMENT_GENERAL`
- `ACCESS_GENERAL`
- `PASSWORD_RECOVERY`
- `SUBSCRIPTION_GENERAL`
- `CERTIFICATION`
- `PERSONAL_ACCOUNT_ISSUE`
- `FINANCIAL_ADVICE_BLOCKED`
- `HUMAN_SUPPORT`
- `UNKNOWN`

## Regras de seguranca

- O chatbot nao recomenda ativos, compra, venda, alocacao, ETF, acoes, fundos, cripto ou outros instrumentos financeiros.
- Pedidos de recomendacao financeira sao bloqueados com uma resposta educada e educativa.
- Perguntas sobre formacoes, pagamentos gerais, subscricoes gerais e acesso geral sao respondidas pela base de conhecimento local.
- Perguntas que envolvam conta individual, pagamento especifico, compra especifica ou acesso individual sao encaminhadas para suporte humano.
- A Fase 1 nao consulta nem solicita dados pessoais.

## Preparacao para Fase 2

A API ja inclui interfaces para evolucao:

- `ShopifyCustomerService`
- `ShopifyOrderService`
- `LlmService`

Estas interfaces permitem adicionar consulta futura a Shopify Admin GraphQL API e/ou um LLM sem alterar o contrato HTTP principal.
