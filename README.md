# MineBanco 🏦

Sistema bancário em **Java 11 + Spring Boot 2.7 + PostgreSQL + Angular 17**.

## Executar

```cmd
executar.bat spring    → http://localhost:8081
cd frontend && npx ng serve    → http://localhost:4200
executar.bat           → console interativo
executar.bat test      → testes unitários
```

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

**Backend:** Java 11, Spring Boot 2.7, PostgreSQL, HikariCP, Maven  
**Frontend:** Angular 17, Angular Material, CSS Grid/Flexbox, responsivo
