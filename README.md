# MineBanco

Sistema bancário de estudo em **Java 21 + Spring Boot 3.3 + PostgreSQL + Angular 17**.

> Projeto educacional, sem autenticação/autorização. Não use em produção ou exposto publicamente sem antes implementar segurança.

## Pré-requisitos

- JDK 21
- Maven 3.9+
- PostgreSQL 16 (ou Docker)
- Node.js 18.13+ / 20+ (apenas para o frontend)

## Banco de dados

Crie o banco e as tabelas:

```bash
createdb minebanco
psql -d minebanco -f banco.sql
```

Configuração padrão (pode ser sobrescrita por variáveis de ambiente):

| Variável | Padrão |
|----------|--------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/minebanco` |
| `DB_USER` | `postgres` |
| `DB_PASSWORD` | `postgres` |

## Executar

```cmd
executar.bat spring    → API em http://localhost:8081
executar.bat frontend  → Angular dev em http://localhost:4200 (faz proxy de /api para 8081)
executar.bat test      → testes unitários (JUnit 5)
executar.bat build     → build completo do backend
```

Ou com Docker (sobe PostgreSQL + API):

```bash
docker compose up --build
```

### Servir o frontend pelo backend

O Angular compila direto para `src/main/resources/static`:

```bash
cd frontend
npm install
npm run build
```

Depois inicie o backend (`executar.bat spring`) e acesse http://localhost:8081.

- Swagger UI: http://localhost:8081/swagger-ui.html
- OpenAPI JSON: http://localhost:8081/api-docs

## API

| Método | Rota | Função |
|--------|------|--------|
| GET | `/api/contas` | Listar |
| GET | `/api/contas/{n}` | Buscar |
| POST | `/api/contas` | Criar |
| POST | `/api/contas/{n}/deposito` | Depositar |
| POST | `/api/contas/{n}/saque` | Sacar |
| POST | `/api/contas/transferencia` | Transferir |
| POST | `/api/contas/rendimento` | Rendimento |
| GET | `/api/contas/impostos` | Impostos |
| DELETE | `/api/contas/{n}` | Excluir (saldo zerado) |

## Estrutura

```
src/               → Backend (Java: controller, model, service, util)
frontend/src/app/  → Frontend (Angular: layout, páginas, services, models)
```

## Stack

**Backend:** Java 21, Spring Boot 3.3.5, PostgreSQL, HikariCP, Maven  
**Frontend:** Angular 17, Angular Material, CSS Grid/Flexbox, responsivo
