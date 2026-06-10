# SIGEVI — Sistema de Gestão de Vistorias Imobiliárias

Integrantes: 
Pedro Christovam Marques RA: 124117694 
Gabriel Alves Bragança RA: 124221803
Vinicius Francisco Silva RA: 1252211622482 
Pedro Henrique Silva Lopes RA: 11914300
João Vitor Romani Nogueira de Paula RA: 124110378

API REST corporativa em **Java 21** + **Spring Boot 3** para gestão de imóveis, vistorias, fotos, relatórios PDF e auditoria.

> **Como iniciar o sistema (comandos + URLs front/back):** [docs/COMO-INICIAR.md](docs/COMO-INICIAR.md)  
> **Documentação acadêmica (trabalho de Engenharia de Software):** [docs/DOCUMENTACAO-SIGEVI.md](docs/DOCUMENTACAO-SIGEVI.md)  
> **Checklist do anúncio da UC:** [docs/CHECKLIST-TRABALHO.md](docs/CHECKLIST-TRABALHO.md)

## Stack

| Tecnologia | Uso |
|------------|-----|
| Java 21 | Linguagem |
| Spring Boot 3.3 | Framework |
| Spring Security + JWT | Autenticação |
| Spring Data JPA | Persistência |
| PostgreSQL + Flyway | Banco e migrations |
| Lombok | Redução de boilerplate |
| OpenPDF | Geração de PDF |
| SpringDoc OpenAPI | Swagger |
| JUnit 5 + Mockito | Testes unitários |

## Estrutura de pastas

```
trabalhofinal/
├── docs/                    # documentação (.md) — fora do código
├── README.md
└── sigevi/                  # aplicação Spring Boot
    ├── src/main/java/br/com/sigevi/
    │   ├── config/
    │   ├── controller/
    │   ├── dto/
    │   ├── exception/
    │   ├── mapper/
    │   ├── model/
    │   ├── pattern/
    │   ├── repository/
    │   ├── security/
    │   ├── service/
    │   └── validator/
    ├── src/main/resources/
    │   ├── static/          # frontend (HTML/CSS/JS)
    │   └── db/migration/
    ├── src/test/java/
    ├── scripts/
    └── pom.xml
```

## Arquitetura

Arquitetura em **camadas** com separação clara:

```
Cliente → Controller → Service → Repository → PostgreSQL
              ↓           ↓
            DTO       Domain/Entity
```

- **Controllers**: apenas HTTP, validação de entrada (`@Valid`), delegação.
- **Services**: regras de negócio, transações, auditoria.
- **Repositories**: acesso a dados (DIP — services dependem de abstrações).
- **DTOs**: contrato da API desacoplado das entidades JPA.

## SOLID no projeto

| Princípio | Aplicação |
|-----------|-----------|
| **SRP** | `ImagemValidator`, `StatusVistoriaValidator`, controllers finos, services por agregado |
| **OCP** | `RelatorioGeracaoStrategy` — novos tipos de relatório sem alterar o contexto |
| **LSP** | Estratégias de relatório intercambiáveis via interface comum |
| **ISP** | `AuditoriaListener` com contrato mínimo (`onAuditoriaEvent`) |
| **DIP** | Services injetam interfaces `*Repository`; Security usa `UserDetailsService` |

## Design Patterns

| Pattern | Onde |
|---------|------|
| **Repository** | `repository/*Repository.java` |
| **DTO** | `dto/request`, `dto/response`, `mapper/*` |
| **Strategy** | `pattern/strategy/Relatorio*Strategy` |
| **Factory** | `pattern/factory/RelatorioFactory`, `AuditoriaFactory` |
| **Builder** | Lombok `@Builder` nas entidades |
| **Singleton** | `JwtPropertiesHolder` (bean singleton Spring) |
| **Observer** | `AuditoriaPublisher` + `AuditoriaPersistListener` |

## Relacionamentos JPA

```
Usuario 1───* Vistoria (inspetor)
Imovel  1───* Vistoria
Vistoria 1──* Foto
Vistoria 1──* Relatorio
Usuario 1───* Relatorio (geradoPor)
Usuario 1───* Auditoria (opcional)
```

## Fluxo de autenticação JWT

```mermaid
sequenceDiagram
    participant C as Cliente
    participant A as AuthController
    participant S as AuthService
    participant AM as AuthenticationManager
    participant J as JwtTokenProvider
    participant F as JwtAuthenticationFilter

    C->>A: POST /auth/login {email, senha}
    A->>S: login()
    S->>AM: authenticate()
    AM-->>S: OK
    S->>J: generateToken()
    J-->>C: Bearer JWT

    C->>F: Header Authorization Bearer
    F->>J: isTokenValid()
    F-->>C: Acesso aos endpoints protegidos
```

## Endpoints REST

Base: `http://localhost:8080/api`

### Autenticação (público)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/auth/login` | Login → JWT |

### Usuários (JWT — cadastro ADMIN)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/usuarios` | Cadastrar |
| PUT | `/usuarios/{id}` | Atualizar |
| GET | `/usuarios` | Listar ativos |
| PATCH | `/usuarios/{id}/desativar` | Desativar |

### Imóveis
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/imoveis` | Cadastrar |
| GET | `/imoveis/{id}` | Buscar por ID |
| PUT | `/imoveis/{id}` | Atualizar |
| GET | `/imoveis/matricula/{matricula}` | Por matrícula |
| GET | `/imoveis/endereco?q=` | Por endereço |

### Vistorias
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/vistorias` | Criar |
| GET | `/vistorias/{id}` | Buscar |
| PATCH | `/vistorias/{id}/status` | Alterar status |
| PATCH | `/vistorias/{id}/observacoes` | Observações |
| GET | `/vistorias/imovel/{imovelId}` | Listar por imóvel |

### Fotos
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/fotos/vistoria/{id}` | Upload multipart |
| GET | `/fotos/vistoria/{id}` | Listar |
| GET | `/fotos/{id}/download` | Download |

### Relatórios
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/relatorios/vistoria/{id}` | Gerar PDF `{tipo: RESUMIDO\|COMPLETO}` |
| GET | `/relatorios/vistoria/{id}` | Listar |
| GET | `/relatorios/{id}/download` | Download PDF |

### Auditoria (ADMIN)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/auditorias/{entidade}/{entidadeId}` | Histórico |

## Swagger

- UI: http://localhost:8080/api/swagger-ui.html
- OpenAPI: http://localhost:8080/api/v3/api-docs

Use **Authorize** com: `Bearer <seu-jwt>`.

## Como executar

Guia completo com **todos os comandos**, URLs do **frontend**, **Swagger**, **H2** e troubleshooting:

**[docs/COMO-INICIAR.md](docs/COMO-INICIAR.md)**

**Resumo rápido (Windows + H2):**

```powershell
cd C:\Users\teste\Desktop\trabalhofinal\sigevi
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

| O quê | URL |
|-------|-----|
| Login (front) | http://localhost:8080/api/index.html |
| Painel (front) | http://localhost:8080/api/app.html |
| Swagger (back) | http://localhost:8080/api/swagger-ui.html |

**Usuário inicial:** `admin@sigevi.com` / `Admin@123`

## Variáveis de ambiente

| Variável | Descrição |
|----------|-----------|
| `JWT_SECRET` | Chave Base64 para assinatura JWT |
| `UPLOAD_DIR` | Diretório de uploads |

## Evolução sugerida

- Paginação (`Pageable`) nos listagens
- Refresh token
- Armazenamento S3 para fotos
- Eventos assíncronos (Spring Events / Kafka) na auditoria
- Testes de integração com Testcontainers
