# Personal Finance Manager

Sistema completo de gestão de finanças pessoais, com autenticação, categorização, orçamentos, relatórios e importação de extratos.

## Acesso

| Ambiente | URL |
|---|---|
| Frontend | https://personal-finance-manager-front.onrender.com |
| API | https://personal-finance-manager-9p6v.onrender.com |
| Swagger UI | https://personal-finance-manager-9p6v.onrender.com/swagger-ui/index.html |

## Funcionalidades

- Registro de receitas e despesas
- Categorização de transações (manual ou automática via CSV)
- Controle de orçamento mensal por categoria
- Dashboard com gráficos de distribuição de despesas
- Relatórios financeiros por período
- Importação de transações via CSV
- Autenticação com JWT (access token + refresh token)
- Controle de acesso por usuário — cada usuário acessa apenas os próprios dados

## Tecnologias

**Backend**
- Java 21 + Spring Boot 3
- Spring Security + JWT (jjwt)
- Spring Data JPA + Hibernate
- PostgreSQL + Flyway
- Springdoc OpenAPI (Swagger)

**Frontend**
- Angular 17 (standalone components)
- Angular Material
- ng2-charts (Chart.js)

**Infraestrutura**
- Docker + Docker Compose
- Deploy no Render (backend como Web Service, frontend como Static Site)

## Rodando localmente

**Pré-requisitos:** Docker e Docker Compose instalados.

```bash
git clone <repositorio>
cd PFM
cp .env.example .env
# edite o .env com suas credenciais
docker compose up --build -d
```

Acesse em `http://localhost:4200`.

## Variáveis de ambiente

Veja o arquivo `.env.example` para a lista completa de variáveis necessárias.

## Testes

```bash
./mvnw test
```

Utiliza JUnit 5, Mockito, AssertJ, Testcontainers e MockMvc.
