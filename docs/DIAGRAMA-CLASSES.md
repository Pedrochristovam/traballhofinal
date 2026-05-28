# SIGEVI — Diagrama de Classes

Diagramas para o trabalho de Engenharia de Software. Podem ser visualizados no GitHub, no VS Code (extensão Mermaid) ou em https://mermaid.live

---

## Figura 1 — Modelo de domínio (dados e relacionamentos)

Representa **o que o sistema armazena** e como as entidades se relacionam.

```mermaid
classDiagram
    direction TB

  class BaseEntity {
    <<abstract>>
    #LocalDateTime criadoEm
    #LocalDateTime atualizadoEm
  }

  class Usuario {
    -Long id
    -String nome
    -String email
    -String senha
    -RoleUsuario role
    -boolean ativo
  }

  class Imovel {
    -Long id
    -String matricula
    -String endereco
    -String cidade
    -String estado
    -String cep
    -TipoImovel tipo
    -BigDecimal areaM2
    -String descricao
  }

  class Vistoria {
    -Long id
    -StatusVistoria status
    -LocalDate dataVistoria
    -String observacoes
  }

  class Foto {
    -Long id
    -String nomeArquivo
    -String caminho
    -String contentType
    -Long tamanhoBytes
    -LocalDateTime criadoEm
  }

  class Relatorio {
    -Long id
    -TipoRelatorio tipo
    -String caminhoArquivo
    -LocalDateTime criadoEm
  }

  class Auditoria {
    -Long id
    -String entidade
    -Long entidadeId
    -AcaoAuditoria acao
    -String valorAnterior
    -String valorNovo
    -LocalDateTime criadoEm
  }

  class RoleUsuario {
    <<enumeration>>
    ADMIN
    USER
  }

  class TipoImovel {
    <<enumeration>>
    RESIDENCIAL
    COMERCIAL
    INDUSTRIAL
    TERRENO
    RURAL
  }

  class StatusVistoria {
    <<enumeration>>
    AGENDADA
    EM_ANDAMENTO
    CONCLUIDA
    CANCELADA
    REPROVADA
  }

  class TipoRelatorio {
    <<enumeration>>
    RESUMIDO
    COMPLETO
  }

  class AcaoAuditoria {
    <<enumeration>>
    CRIACAO
    ATUALIZACAO
    EXCLUSAO
    STATUS_ALTERADO
    LOGIN
  }

  BaseEntity <|-- Usuario
  BaseEntity <|-- Imovel
  BaseEntity <|-- Vistoria

  Usuario --> RoleUsuario
  Imovel --> TipoImovel
  Vistoria --> StatusVistoria
  Relatorio --> TipoRelatorio
  Auditoria --> AcaoAuditoria

  Imovel "1" --> "*" Vistoria : possui
  Usuario "1" --> "*" Vistoria : inspeta
  Vistoria "1" --> "*" Foto : anexa
  Vistoria "1" --> "*" Relatorio : gera
  Usuario "1" --> "*" Relatorio : geradoPor
  Usuario "0..1" --> "*" Auditoria : registrou
```

---

## Figura 2 — Camadas da aplicação por funcionalidade (RF)

Representa **como cada funcionalidade é implementada** nas camadas Controller → Service → Repository.

```mermaid
classDiagram
    direction TB

  %% ========== RF01 AUTENTICAÇÃO ==========
  class AuthController {
    +login(LoginRequest) LoginResponse
  }
  class AuthService {
    +login(LoginRequest) LoginResponse
  }
  class JwtTokenProvider {
    +generateToken(UserDetails) String
    +isTokenValid(token, user) boolean
  }
  class JwtAuthenticationFilter {
    +doFilterInternal()
  }
  class CustomUserDetailsService {
    +loadUserByUsername(email) UserDetails
  }

  AuthController --> AuthService
  AuthService --> JwtTokenProvider
  AuthService --> CustomUserDetailsService
  JwtAuthenticationFilter --> JwtTokenProvider
  JwtAuthenticationFilter --> CustomUserDetailsService

  %% ========== RF02 USUÁRIOS ==========
  class UsuarioController {
    +cadastrar()
    +atualizar()
    +listar()
    +desativar()
  }
  class UsuarioService {
    +cadastrar()
    +atualizar()
    +listarAtivos()
    +desativar()
  }
  class UsuarioRepository {
    <<interface>>
    +findByEmail()
    +existsByEmail()
  }
  class UsuarioMapper {
    +toResponse()
    +toEntity()
  }

  UsuarioController --> UsuarioService
  UsuarioService --> UsuarioRepository
  UsuarioService --> UsuarioMapper

  %% ========== RF03 IMÓVEIS ==========
  class ImovelController {
    +cadastrar()
    +buscarPorId()
    +atualizar()
    +buscarPorMatricula()
    +buscarPorEndereco()
  }
  class ImovelService {
    +cadastrar()
    +buscarPorMatricula()
    +buscarPorEndereco()
  }
  class ImovelRepository {
    <<interface>>
    +findByMatricula()
  }

  ImovelController --> ImovelService
  ImovelService --> ImovelRepository
  ImovelService --> ImovelMapper

  %% ========== RF04 e RF05 VISTORIAS ==========
  class VistoriaController {
    +criar()
    +alterarStatus()
    +adicionarObservacoes()
    +listarPorImovel()
  }
  class VistoriaService {
    +criar()
    +alterarStatus()
    +adicionarObservacoes()
  }
  class StatusVistoriaValidator {
    +validarTransicao(atual, novo)
  }
  class VistoriaRepository {
    <<interface>>
    +findByImovelId()
  }

  VistoriaController --> VistoriaService
  VistoriaService --> VistoriaRepository
  VistoriaService --> ImovelService
  VistoriaService --> UsuarioService
  VistoriaService --> StatusVistoriaValidator

  %% ========== RF06 FOTOS ==========
  class FotoController {
    +upload()
    +listar()
    +download()
  }
  class FotoService {
    +upload()
    +listarPorVistoria()
    +download()
  }
  class ImagemValidator {
    +validar(MultipartFile)
  }

  FotoController --> FotoService
  FotoService --> VistoriaService
  FotoService --> ImagemValidator
  FotoService --> FotoRepository

  %% ========== RF07 RELATÓRIOS PDF ==========
  class RelatorioController {
    +gerar()
    +listar()
    +download()
  }
  class RelatorioService {
    +gerar()
    +listarPorVistoria()
  }
  class RelatorioStrategyContext {
    +gerar(tipo, vistoria, dir)
  }
  class RelatorioGeracaoStrategy {
    <<interface>>
    +gerar()
  }
  class RelatorioResumidoStrategy
  class RelatorioCompletoStrategy
  class RelatorioFactory {
    <<utility>>
    +criar()
  }

  RelatorioController --> RelatorioService
  RelatorioService --> RelatorioStrategyContext
  RelatorioStrategyContext --> RelatorioGeracaoStrategy
  RelatorioResumidoStrategy ..|> RelatorioGeracaoStrategy
  RelatorioCompletoStrategy ..|> RelatorioGeracaoStrategy
  RelatorioService --> RelatorioFactory

  %% ========== RF08 AUDITORIA ==========
  class AuditoriaController {
    +listarHistorico()
  }
  class AuditoriaService {
    +registrar()
    +listarHistorico()
  }
  class AuditoriaPublisher {
    +publish(AuditoriaEvent)
  }
  class AuditoriaPersistListener {
    +onAuditoriaEvent()
  }
  class AuditoriaFactory {
    <<utility>>
    +fromEvent()
  }

  AuditoriaController --> AuditoriaService
  UsuarioService --> AuditoriaService
  ImovelService --> AuditoriaService
  VistoriaService --> AuditoriaService
  FotoService --> AuditoriaService
  RelatorioService --> AuditoriaService
  AuthService --> AuditoriaService
  AuditoriaService --> AuditoriaPublisher
  AuditoriaPublisher --> AuditoriaPersistListener
  AuditoriaPersistListener --> AuditoriaFactory
  AuditoriaPersistListener --> AuditoriaRepository

  %% ========== EXCEÇÕES GLOBAIS ==========
  class GlobalExceptionHandler {
    +handleNotFound()
    +handleBusiness()
    +handleValidation()
  }
```

---

## Figura 3 — Visão simplificada (para slide da apresentação)

Versão resumida ligando **módulos funcionais** ao domínio.

```mermaid
classDiagram
    direction LR

  package Autenticacao {
    class AuthController
    class AuthService
    class JwtTokenProvider
  }

  package Usuarios {
    class UsuarioController
    class UsuarioService
  }

  package Imoveis {
    class ImovelController
    class ImovelService
    class Imovel
  }

  package Vistorias {
    class VistoriaController
    class VistoriaService
    class Vistoria
    class StatusVistoriaValidator
  }

  package Fotos {
    class FotoController
    class FotoService
    class Foto
    class ImagemValidator
  }

  package Relatorios {
    class RelatorioController
    class RelatorioService
    class RelatorioGeracaoStrategy
  }

  package Auditoria {
    class AuditoriaController
    class AuditoriaService
    class AuditoriaPublisher
  }

  AuthController --> AuthService
  UsuarioController --> UsuarioService
  ImovelController --> ImovelService
  VistoriaController --> VistoriaService
  FotoController --> FotoService
  RelatorioController --> RelatorioService
  AuditoriaController --> AuditoriaService

  ImovelService --> Imovel
  VistoriaService --> Vistoria
  FotoService --> Foto
  RelatorioService --> Relatorio
  Vistoria --> Imovel
  Vistoria --> Foto
  Vistoria --> Relatorio
```

---

## Legenda: funcionalidade × classes

| RF | Funcionalidade | Classes principais |
|----|----------------|-------------------|
| RF01 | Login JWT | `AuthController`, `AuthService`, `JwtTokenProvider`, `JwtAuthenticationFilter` |
| RF02 | Usuários | `UsuarioController`, `UsuarioService`, `UsuarioRepository` |
| RF03 | Imóveis | `ImovelController`, `ImovelService`, `Imovel`, `ImovelRepository` |
| RF04 | Vistorias | `VistoriaController`, `VistoriaService`, `Vistoria` |
| RF05 | Status vistoria | `StatusVistoriaValidator`, `VistoriaService.alterarStatus()` |
| RF06 | Fotos | `FotoController`, `FotoService`, `ImagemValidator`, `Foto` |
| RF07 | PDF | `RelatorioController`, `RelatorioService`, `Relatorio*Strategy` |
| RF08 | Auditoria | `AuditoriaService`, `AuditoriaPublisher`, `AuditoriaPersistListener` |

---

## Como exportar para PDF/imagem (entrega)

1. Copie o bloco Mermaid desejado  
2. Cole em https://mermaid.live  
3. Exporte como **PNG** ou **SVG**  
4. Inclua na pasta `docs/diagramas/` do repositório  
