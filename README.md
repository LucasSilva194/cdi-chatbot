# CDI Chatbot

Chatbot de suporte ao cliente para a Ciencias do Investimento, dividido em duas aplicacoes:

- `ui`: Vue 3 + TypeScript + Vite
- `api`: Java 21 + Spring Boot + PostgreSQL

A Fase 1 responde a perguntas frequentes sem consultar dados pessoais. Perguntas sobre conta, pagamento individual, compras especificas ou acesso individual sao encaminhadas para suporte humano. Pedidos de aconselhamento financeiro sao bloqueados de forma educada.

## Arquitetura

```text
UI Vue
  -> Spring Boot API
    -> FAQ/RAG Service local
    -> PostgreSQL
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

Opcionalmente, pode iniciar Redis para evolucao futura:

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
