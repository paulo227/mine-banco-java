# MineBanco

Sistema bancário de estudo: back-end em **Java 21 + Spring Boot 3.3.5 + PostgreSQL** e front-end em **Angular 17 + Material**.

> Projeto educacional. Não possui autenticação/autorização; não use em produção.

## Stack

- **Back-end:** Java 21, Spring Boot (Web, JDBC, Validation), JDBC puro com DAO, HikariCP, PostgreSQL, springdoc/Swagger.
- **Front-end:** Angular 17 standalone, Angular Material, tema claro/escuro e acessibilidade.
- **Build:** Maven 3.9+; testes com JUnit 5.
- **Infra:** Docker multi-stage + Docker Compose (PostgreSQL 16 + API).

## Como rodar

Pré-requisitos: JDK 21, Maven, PostgreSQL 16+ (ou Docker) e Node 18+ para o front-end.

```bash
# 1. Banco local
createdb minebanco
psql -d minebanco -f banco.sql

# 2. API (http://localhost:8081)
mvn spring-boot:run

# 3. Front-end em dev (http://localhost:4200, proxy para 8081)
cd frontend
npm install
npm start
```

Ou com Docker: `docker compose up --build`.

Para servir a SPA pelo back-end, rode `npm run build` em `frontend/` antes de iniciar/empacotar a API; o build vai para `src/main/resources/static`.

Configuração do banco (variáveis de ambiente):

| Variável | Padrão |
|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/minebanco` |
| `DB_USER` | `postgres` |
| `DB_PASSWORD` | `postgres` |

As tabelas (`cliente`, `conta`, `transacao`) também são criadas automaticamente na inicialização.

- Swagger UI: `http://localhost:8081/swagger-ui.html`
- OpenAPI: `http://localhost:8081/api-docs`

## API

Base: `/api/contas`

| Método | Rota | Ação |
|---|---|---|
| `GET` | `/api/contas?page=0&size=20` | Lista contas |
| `GET` | `/api/contas/{n}` | Busca conta |
| `POST` | `/api/contas` | Cria conta |
| `DELETE` | `/api/contas/{n}` | Exclui (saldo zero) |
| `POST` | `/api/contas/{n}/deposito` | Deposita |
| `POST` | `/api/contas/{n}/saque` | Saca |
| `POST` | `/api/contas/transferencia` | Transfere |
| `POST` | `/api/contas/rendimento` | Rendimento nas poupanças |
| `GET` | `/api/contas/impostos` | Total de impostos |
| `GET` | `/api/contas/{n}/extrato` | Extrato |

## Regras principais

- **Conta corrente:** taxa de saque de R$ 2,00, pode usar limite e paga 1% de imposto sobre saldo positivo.
- **Poupança:** sem limite, não permite saldo negativo, não paga imposto e aceita rendimento.
- Transferência usa apenas saldo (não o limite) e é gravada de forma atômica.
- Exclusão só com saldo exatamente zero.
- CPF é único; se já existir, o cliente é reutilizado.

## Estrutura

```
src/                    # Back-end (app, controller, service, model, util, exception)
src/test/               # Testes JUnit 5
frontend/src/app/       # Telas, layout, serviços e modelos Angular
banco.sql               # Schema do banco
Dockerfile / docker-compose.yml
```

## Limitações

- Sem login, perfis ou proteção dos endpoints.
- Dinheiro usa `double` no Java (o banco usa `DECIMAL`).
- Sem migrations; schema definido em `banco.sql` e recriado pelo `DatabaseManager`.
- Transferência não grava saldo e histórico na mesma transação SQL.
- Layout Maven não padrão (`src` como source root), com JUnit em escopo `compile`.
- Sem fallback de rotas da SPA quando servida pelo Spring; recarregar `/dashboard` pode dar 404.

Testes: `mvn test` (ou `executar.bat test`).
