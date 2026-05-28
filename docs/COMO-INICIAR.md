# SIGEVI — Como iniciar o sistema

Guia passo a passo para subir o **backend** (API Spring Boot) e acessar o **frontend** (telas HTML integradas), com todos os comandos e URLs.

---

## 1. Pré-requisitos

| Item | Obrigatório? | Como verificar |
|------|--------------|----------------|
| **JDK 21** | Sim | `java -version` |
| **Maven** | Não* | O projeto inclui **Maven Wrapper** (`mvnw` / `mvnw.cmd`) |
| **PostgreSQL** | Só no modo produção | Não precisa se usar perfil **`dev`** |

\* Use `.\mvnw.cmd` no Windows em vez de `mvn`.

**Pasta do projeto (raiz da API):**

```
C:\Users\teste\Desktop\trabalhofinal\sigevi
```

Todos os comandos abaixo assumem que você está **dentro dessa pasta**.

---

## 2. Escolha o modo de execução

| Modo | Banco | Quando usar |
|------|-------|-------------|
| **A — `dev` (recomendado)** | H2 em memória | Testes, demo, trabalho acadêmico — **não instala PostgreSQL** |
| **B — padrão** | PostgreSQL | Ambiente “real” / entrega com banco persistente |

---

## 3. Modo A — Iniciar com H2 (mais rápido)

### 3.1 Abrir o terminal na pasta do projeto

**PowerShell (Windows):**

```powershell
cd C:\Users\teste\Desktop\trabalhofinal\sigevi
```

### 3.2 Subir a aplicação

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

> No PowerShell, as aspas em `"-Dspring-boot.run.profiles=dev"` evitam erro de parsing.

**Aguarde** até aparecer algo como:

```text
Started SigeviApplication in X seconds
```

A API fica disponível na porta **8080**, com prefixo **`/api`**.

### 3.3 Parar a aplicação

No mesmo terminal: **`Ctrl + C`**

Se a porta 8080 estiver ocupada:

```powershell
netstat -ano | findstr :8080
taskkill /PID <numero_do_pid> /F
```

Depois suba de novo o comando da seção 3.2.

---

## 4. Modo B — Iniciar com PostgreSQL

### 4.1 PostgreSQL instalado e rodando

- Serviço ativo na porta **5432**
- Banco e usuário criados (script do projeto):

```powershell
cd C:\Users\teste\Desktop\trabalhofinal\sigevi
psql -U postgres -f scripts\init-database.sql
```

*(Ajuste `-U postgres` se seu usuário for outro.)*

### 4.2 Subir a aplicação (sem perfil dev)

```powershell
cd C:\Users\teste\Desktop\trabalhofinal\sigevi
.\mvnw.cmd spring-boot:run
```

Configuração padrão em `application.yml`:

- URL: `jdbc:postgresql://localhost:5432/sigevi`
- Usuário/senha: `sigevi` / `sigevi`

---

## 5. URLs — Onde acessar tudo

Base da API: **`http://localhost:8080/api`**

### 5.1 Frontend (interface web)

| Tela | URL | Descrição |
|------|-----|-----------|
| **Login** | http://localhost:8080/api/index.html | Entrada no sistema |
| **Painel** | http://localhost:8080/api/app.html | Fluxo: imóvel → vistoria → status → foto → PDF |

**Credenciais padrão (criadas automaticamente na primeira execução):**

| Campo | Valor |
|-------|-------|
| E-mail | `admin@sigevi.com` |
| Senha | `Admin@123` |

**Fluxo sugerido no painel:**

1. Login  
2. Cadastrar imóvel (matrícula **única**, ex.: `MAT-101`)  
3. Criar vistoria (usa o ID do imóvel)  
4. Alterar status (`EM_ANDAMENTO` → `CONCLUIDA`)  
5. Enviar foto (jpg/png/webp)  
6. Gerar relatório → botão **Baixar PDF**

### 5.2 Backend — Documentação da API (Swagger)

| Recurso | URL |
|---------|-----|
| **Swagger UI** | http://localhost:8080/api/swagger-ui.html |
| **OpenAPI JSON** | http://localhost:8080/api/v3/api-docs |

**Como autenticar no Swagger:**

1. `POST /auth/login` com e-mail e senha  
2. Copie **somente o token** (sem a palavra `Bearer`)  
3. Clique em **Authorize** e cole o token  

### 5.3 Backend — Endpoints REST (base)

Todos os endpoints protegidos exigem header:

```http
Authorization: Bearer <seu_token_jwt>
```

| Área | Exemplos de caminho (após `/api`) |
|------|-----------------------------------|
| Auth | `POST /auth/login` |
| Usuários | `/usuarios` |
| Imóveis | `/imoveis`, `/imoveis/{id}` |
| Vistorias | `/vistorias`, `/vistorias/{id}/status` |
| Fotos | `/fotos/vistoria/{id}` |
| Relatórios | `/relatorios/vistoria/{id}`, `/relatorios/{id}/download` |
| Auditoria | `/auditorias/{entidade}/{entidadeId}` |

### 5.4 Console H2 (somente perfil `dev`)

| Recurso | URL |
|---------|-----|
| **H2 Console** | http://localhost:8080/api/h2-console |

**Dados de conexão no H2:**

| Campo | Valor |
|-------|-------|
| JDBC URL | `jdbc:h2:mem:sigevi` |
| User Name | `sa` |
| Password | *(vazio)* |

---

## 6. Comandos úteis

### Rodar testes unitários

```powershell
cd C:\Users\teste\Desktop\trabalhofinal\sigevi
.\mvnw.cmd test
```

### Compilar sem subir o servidor

```powershell
.\mvnw.cmd clean package -DskipTests
```

### Subir pela IDE (VS Code / IntelliJ)

1. Abra a pasta `sigevi`  
2. Classe principal: `br.com.sigevi.SigeviApplication`  
3. **Active profiles:** `dev` (para H2) ou vazio (PostgreSQL)  
4. Run/Debug  

---

## 7. Mapa visual do sistema

```text
┌─────────────────────────────────────────────────────────────┐
│  Navegador                                                   │
│  ┌──────────────────┐    ┌──────────────────────────────┐ │
│  │ Frontend (static) │    │ Swagger UI                    │ │
│  │ /api/index.html   │    │ /api/swagger-ui.html          │ │
│  │ /api/app.html     │    └──────────────────────────────┘ │
│  └────────┬─────────┘                                       │
│           │ fetch + JWT                                      │
└───────────┼─────────────────────────────────────────────────┘
            ▼
┌─────────────────────────────────────────────────────────────┐
│  Spring Boot — http://localhost:8080/api                     │
│  Controllers → Services → Repositories → Banco (H2 ou PG)   │
└─────────────────────────────────────────────────────────────┘
```

---

## 8. Problemas comuns

| Sintoma | Causa provável | Solução |
|---------|----------------|---------|
| `Connection to localhost:5432 refused` | PostgreSQL off ou perfil errado | Use `dev` (seção 3) ou ligue o PostgreSQL |
| Porta 8080 em uso | Instância antiga rodando | `taskkill` no PID (seção 3.3) |
| Swagger retorna 403 | Token com `Bearer` duplicado | Cole **só o token** no Authorize |
| Matrícula duplicada (422) | Imóvel já cadastrado | Use outra matrícula (`MAT-102`, etc.) |
| PDF 403 no navegador | Link direto sem JWT | Use o botão **Baixar PDF** no painel |
| `java` não reconhecido | JDK não instalado / PATH | Instale JDK 21 e reinicie o terminal |

---

## 9. Checklist rápido (demo completa)

- [ ] `cd sigevi`  
- [ ] `.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"`  
- [ ] Abrir http://localhost:8080/api/index.html  
- [ ] Login: `admin@sigevi.com` / `Admin@123`  
- [ ] Painel: http://localhost:8080/api/app.html  
- [ ] Percorrer os 5 passos (imóvel → vistoria → status → foto → PDF)  
- [ ] *(Opcional)* Swagger: http://localhost:8080/api/swagger-ui.html  

---

## 10. Documentação relacionada

| Arquivo | Conteúdo |
|---------|----------|
| [README.md](../README.md) | Visão geral, arquitetura, endpoints |
| [DOCUMENTACAO-SIGEVI.md](DOCUMENTACAO-SIGEVI.md) | Documentação acadêmica completa |
| [CHECKLIST-TRABALHO.md](CHECKLIST-TRABALHO.md) | Requisitos do trabalho |
