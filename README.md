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

## Ambientes

O projeto esta preparado para tres ambientes:

- `local`: desenvolvimento na maquina, normalmente com PostgreSQL local via Docker e estado de conversa em memoria.
- `staging`: ambiente de teste antes de producao, com Redis e uma Shopify Custom App separada da loja real.
- `production`: ambiente publico, com Redis, CORS restrito ao dominio real e tokens Shopify proprios.

Exemplos de variaveis:

- `api/.env.local.example`
- `api/.env.staging.example`
- `api/.env.production.example`
- `ui/.env.local.example`
- `ui/.env.staging.example`
- `ui/.env.production.example`

Copie o ficheiro certo para `.env` em cada ambiente. Nunca reutilize tokens de staging em producao.

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
cp .env.local.example .env
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
cp .env.local.example .env
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

### Build do widget embutivel

Para gerar o bundle pensado para ser carregado no site:

```bash
cd ui
npm run build:widget
```

O output fica em `ui/dist-widget`:

- `cdi-chatbot-widget.js`
- `style.css`

Exemplo de embed:

```html
<link rel="stylesheet" href="https://widget.cienciasdoinvestimento.com/widget/style.css">
<script
  src="https://widget.cienciasdoinvestimento.com/widget/cdi-chatbot-widget.js"
  data-api-base-url="https://api.cienciasdoinvestimento.com"
  data-avatar-url="https://widget.cienciasdoinvestimento.com/cdi-chatbot-avatar.png"
  defer
></script>
```

Em staging, use os dominios de staging:

```html
<link rel="stylesheet" href="https://staging-widget.cienciasdoinvestimento.com/widget/style.css">
<script
  src="https://staging-widget.cienciasdoinvestimento.com/widget/cdi-chatbot-widget.js"
  data-api-base-url="https://staging-api.cienciasdoinvestimento.com"
  data-avatar-url="https://staging-widget.cienciasdoinvestimento.com/cdi-chatbot-avatar.png"
  defer
></script>
```

Para testar na Shopify sem impactar clientes, coloque este embed primeiro numa theme duplicada ou num ambiente de desenvolvimento.

## Docker

Para correr apenas infraestrutura local:

```bash
docker compose up -d postgres
```

Para correr API e UI em containers:

```bash
cd api
./mvnw -DskipTests package
cd ..
cd ui
npm run build
npm run build:widget
cd ..
docker compose --profile app up --build
```

Enderecos locais:

- API: `http://localhost:8080`
- UI/widget static preview: `http://localhost:4173`

O Redis continua opcional em local:

```bash
docker compose --profile redis up -d redis
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
- A aplicacao e estritamente read-only: nunca deve criar, alterar, apagar, cancelar, reembolsar ou submeter dados em Shopify, PostgreSQL, ferramentas internas, LLMs ou qualquer outro sistema externo.
- Se uma resposta exigir uma acao de escrita, o chatbot deve explicar que nao consegue executar essa acao e encaminhar para suporte humano.

## Preparacao para Fase 2

A API ja inclui interfaces para evolucao:

- `ShopifyCustomerService`
- `ShopifyOrderService`
- `LlmService`

Estas interfaces permitem adicionar consulta futura a Shopify Admin GraphQL API e/ou um LLM sem alterar o contrato HTTP principal.

### Shopify Custom App read-only

A futura Custom App da Shopify deve ser configurada apenas com permissoes de consulta. Para o MVP, os scopes esperados sao:

```env
SHOPIFY_ADMIN_API_SCOPES=read_customers,read_orders,read_products
```

Qualquer scope `write_*` e proibido. A integracao tambem deve usar apenas operacoes GraphQL `query`; operacoes `mutation` sao bloqueadas pelo `ShopifyReadOnlyGuard`.

Operacoes proibidas incluem, sem excecao: criar ou editar clientes, editar encomendas, cancelar encomendas, emitir reembolsos, alterar subscricoes, atualizar produtos, criar descontos, alterar metafields, enviar emails transacionais ou marcar acessos como ativos. O chatbot pode consultar informacao permitida e orientar o cliente, mas a execucao de alteracoes fica sempre fora da aplicacao.

Para testar novas versoes:

- use uma branch `develop` para staging e `main` para producao;
- crie uma Shopify Custom App separada para staging;
- use uma development store ou theme duplicada para validar o widget;
- mantenha tokens, dominios, CORS e base URLs separados entre staging e production.

## Fluxo de staging

A branch `develop` e a base para testar novas versoes antes de `main`.

O workflow `.github/workflows/staging.yml` faz:

- testes da API;
- build da UI e do widget;
- package da API;
- build e publish das imagens Docker para GHCR;
- deploy opcional por SSH, apenas se `STAGING_DEPLOY_ENABLED=true`.

Os ficheiros de suporte estao em `deploy/staging`:

- `docker-compose.staging.yml`
- `.env.staging.example`
- `README.md`

Para staging na Shopify, use uma Custom App propria de staging, com tokens separados e scopes apenas `read_*`.
