# Melhorias Técnicas — SIGEVI

Relatório das melhorias implementadas na API Spring Boot do SIGEVI (Sistema de Gestão de Vistorias Imobiliárias).

---

## 1. Problemas identificados

### Segurança crítica
- Usuários com role `USER` podiam **atualizar** e **desativar** qualquer conta via `PUT/PATCH /usuarios/**`, inclusive alterar `role` para `ADMIN` (escalação de privilégios).
- Tokens JWT inválidos ou expirados causavam **HTTP 500** em vez de **401**.
- A API expunha o **caminho interno** do arquivo PDF (`caminhoArquivo`) nos responses de relatório.

### Bugs e inconsistências
- **Auditoria de usuários** registrava o ID do usuário alvo como executor, não o admin logado.
- **`expiresIn`** no login estava hardcoded (`86400`) e não refletia `sigevi.jwt.expiration-ms`.
- **Atualização de imóvel** não validava matrícula duplicada (falha silenciosa no banco → 500).
- **Download de relatório** não verificava existência do arquivo no disco (diferente de fotos).
- Controllers de download consultavam repositórios **duas vezes** (service + controller).

### Código morto e manutenção
- `JwtPropertiesHolder` (singleton estático) não era usado em lugar nenhum.
- `@EnableMethodSecurity` estava ativo, mas nenhum endpoint usava `@PreAuthorize`.
- `AuthService` capturava `Exception` genérica no login, mascarando erros reais.

---

## 2. Alterações realizadas

### Autorização de usuários
- `SecurityConfig`: `PUT` e `PATCH` em `/usuarios/**` restritos a `ADMIN`; `GET` continua `ADMIN` ou `USER`.
- `UsuarioController`: `@PreAuthorize` em todos os endpoints + `SecurityUtils` para identificar o executor.
- `UsuarioService`: métodos recebem `usuarioExecutorId` para auditoria correta.

### JWT e erros
- `JwtAuthenticationFilter`: captura `JwtException` e responde **401** com JSON padronizado (`ErrorResponse`).
- `GlobalExceptionHandler`: handler para `DataIntegrityViolationException` → **422**.
- `AuthService`: captura apenas `BadCredentialsException`; `expiresIn` derivado de `JwtProperties`.

### Integridade de dados
- `ImovelRepository`: método `existsByMatriculaAndIdNot`.
- `ImovelService.atualizar`: validação de matrícula duplicada antes do save.

### Downloads e API de relatórios
- Novo record `FileDownloadResult` (resource + contentType + filename).
- `FotoService` e `RelatorioService` retornam metadados completos no download.
- Controllers de foto/relatório **não injetam mais repositórios**.
- `RelatorioResponse`: campo `caminhoArquivo` substituído por `url` (endpoint de download).

### Limpeza
- Removido `JwtPropertiesHolder.java`.

### Testes adicionados/atualizados
- `UsuarioServiceTest`: valida executor na auditoria.
- `AuthServiceTest`: valida `expiresIn` dinâmico.
- `ImovelServiceTest`: rejeição de matrícula duplicada na atualização.
- `JwtAuthenticationFilterTest`: token inválido → 401, token válido → autenticação.

---

## 3. Benefícios

| Melhoria | Benefício |
|----------|-----------|
| Autorização ADMIN em usuários | Elimina escalação de privilégios |
| JWT 401 padronizado | Front-end e clientes tratam sessão expirada corretamente |
| Auditoria com executor real | Rastreabilidade confiável de quem fez a ação |
| Validação de matrícula | Erro 422 claro em vez de 500 do banco |
| Download unificado | Menos duplicação, comportamento consistente |
| URL em vez de path interno | Menor exposição de infraestrutura |
| Testes de segurança | Regressão detectável em CI |

---

## 4. Impactos no sistema

- **Breaking change leve:** clientes que consumiam `caminhoArquivo` em `RelatorioResponse` devem usar o campo `url`.
- **Comportamento:** usuários `USER` recebem **403 Forbidden** ao tentar `PUT/PATCH /usuarios/**` (antes conseguiam).
- **Login:** `expiresIn` continua em segundos, agora sincronizado com a configuração YAML.
- **Frontend (`app.js`):** não usa `caminhoArquivo`; **sem impacto** na interface atual.

---

## 5. Arquivos modificados

**Criados:**
- `dto/response/FileDownloadResult.java`
- `exception/InvalidTokenException.java`
- `test/.../JwtAuthenticationFilterTest.java`
- `docs/MELHORIAS-TECNICAS.md` (este arquivo)

**Modificados:**
- `config/SecurityConfig.java`
- `controller/UsuarioController.java`
- `controller/FotoController.java`
- `controller/RelatorioController.java`
- `service/UsuarioService.java`
- `service/ImovelService.java`
- `service/AuthService.java`
- `service/FotoService.java`
- `service/RelatorioService.java`
- `security/JwtAuthenticationFilter.java`
- `exception/GlobalExceptionHandler.java`
- `repository/ImovelRepository.java`
- `dto/response/RelatorioResponse.java`
- `mapper/RelatorioMapper.java`
- `test/.../AuthServiceTest.java`
- `test/.../UsuarioServiceTest.java`
- `test/.../ImovelServiceTest.java`

**Removidos:**
- `config/JwtPropertiesHolder.java`

---

## 6. Sugestões futuras

1. **Testes de integração** com `@SpringBootTest` + `MockMvc` para fluxos de autorização end-to-end.
2. **Claims JWT** com `userId` e `roles` para evitar consulta dupla ao banco por request.
3. **Validação de imagem** por magic bytes, não só `Content-Type`.
4. **Rate limiting** em `/auth/login` contra brute force.
5. **Externalizar** `baseUrl` (`/api`) via `@Value("${server.servlet.context-path}")` em mappers.
6. **Refatorar strategies PDF** para extrair boilerplate OpenPDF comum.
7. **Integração tests** com Testcontainers + PostgreSQL para validar Flyway e constraints.

---

## Como validar

```powershell
cd sigevi
.\mvnw.cmd test
```

Todos os testes unitários devem passar (`BUILD SUCCESS`).
