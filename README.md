# Big Chat Brasil

[![Backend CI](https://github.com/fabiopinheirobatista/techchallenge-irrah-fullstack/actions/workflows/backend-ci.yml/badge.svg?branch=developer)](https://github.com/fabiopinheirobatista/techchallenge-irrah-fullstack/actions/workflows/backend-ci.yml)
[![Frontend CI](https://github.com/fabiopinheirobatista/techchallenge-irrah-fullstack/actions/workflows/frontend-ci.yml/badge.svg?branch=developer)](https://github.com/fabiopinheirobatista/techchallenge-irrah-fullstack/actions/workflows/frontend-ci.yml)
[![Docker](https://github.com/fabiopinheirobatista/techchallenge-irrah-fullstack/actions/workflows/docker-publish.yml/badge.svg)](https://github.com/fabiopinheirobatista/techchallenge-irrah-fullstack/actions/workflows/docker-publish.yml)

Aplicação fullstack de mensagens BCB, com autenticação, clientes pré e pós-pagos, conversas, fila persistente com prioridade, histórico financeiro e interface responsiva de chat.

## Tecnologias

- Java 21, Spring Boot 4 e Spring Security
- Spring Data JPA, PostgreSQL 17 e Flyway
- Angular 21, TypeScript, RxJS e SCSS
- Maven, npm, JUnit 6, Vitest, Mockito e Docker Compose

## Executar com Docker

```bash
cp .env.example .env
docker compose up --build
```

- Frontend: `http://localhost:4200`
- API: `http://localhost:8080`

Credenciais de desenvolvimento criadas pelo Flyway:

| Perfil | Documento | Senha |
|---|---|---|
| Administrador | `11222333000181` | `Admin@123` |
| Cliente pré-pago | `52998224725` | `Client@123` |
| Cliente pós-pago | `11144477735` | `Client@123` |

## Fluxo principal da API

1. Autentique com `POST /api/auth` usando `documentId` e `password`.
2. Envie o token retornado nas demais chamadas como `Authorization: Bearer <token>`.
3. Liste ou crie conversas em `/api/conversations`.
4. Consulte e envie mensagens em `/api/conversations/{conversationId}/messages`.

Principais endpoints:

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/auth` | Autenticação |
| `GET` | `/api/clients/me` | Perfil e posição financeira |
| `GET` | `/api/clients/me/transactions` | Histórico financeiro |
| `GET/POST` | `/api/conversations` | Listar ou criar conversas |
| `GET/POST` | `/api/conversations/{id}/messages` | Histórico ou envio de mensagem |
| `POST` | `/api/conversations/{id}/messages/inbound` | Simular mensagem recebida |
| `PATCH` | `/api/conversations/{id}/messages/{messageId}/read` | Marcar recebida como lida |
| `POST` | `/api/admin/clients/{id}/credits` | Adicionar crédito pré-pago |
| `PUT` | `/api/admin/clients/{id}/monthly-limit` | Ajustar limite pós-pago |

Mensagens normais custam R$ 0,25 e urgentes R$ 0,50. Urgentes são processadas antes das normais; dentro da mesma prioridade, a ordem é FIFO.

## Testes

```bash
# Backend
cd techchallenge-irrah-backend
./mvnw --batch-mode --no-transfer-progress verify

# Frontend
cd ../techchallenge-irrah-frontend
npm ci
npm test
npm run build
```

No Windows, também é possível executar `mvn --batch-mode --no-transfer-progress verify` com Maven 3.9+ instalado.
