# Staging deploy

Este fluxo assume um servidor simples com Docker e Docker Compose.

## Primeira configuracao

1. Copiar `docker-compose.staging.yml` para o servidor.
2. Criar um ficheiro `.env` no mesmo diretorio, baseado em `.env.staging.example`.
3. Usar apenas tokens Shopify da Custom App de staging.
4. Garantir que `SHOPIFY_ADMIN_API_SCOPES` contem apenas scopes `read_*`.

## Deploy manual no servidor

```bash
docker login ghcr.io
docker compose pull
docker compose up -d
```

## Deploy via GitHub Actions

O workflow `.github/workflows/staging.yml` corre em push para `develop`.

Para ativar deploy automatico por SSH, configurar:

Repository variable:

- `STAGING_DEPLOY_ENABLED=true`
- `STAGING_API_BASE_URL=https://staging-api.cienciasdoinvestimento.com`
- `STAGING_SSH_HOST`
- `STAGING_SSH_USER`
- `STAGING_DEPLOY_PATH`

Repository secret:

- `STAGING_SSH_KEY`

Sem `STAGING_DEPLOY_ENABLED=true`, o workflow valida e publica imagens, mas nao faz deploy automatico.
