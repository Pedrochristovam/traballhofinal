# Checklist do Trabalho — SIGEVI × Anúncio da UC

Documento de acompanhamento do grupo em relação ao enunciado de **Engenharia de Software (prática e teoria)**.

**Repositório:** https://github.com/Pedrochristovam/traballhofinal  
**Projeto:** SIGEVI — Sistema de Gestão de Vistorias Imobiliárias  

**Legenda:** ✅ Feito | ⚠️ Parcial | ❌ Falta

---

## 1. Definição do problema

| Item do anúncio | Status | Onde / observação |
|-----------------|--------|-------------------|
| Problema real identificado | ✅ | `docs/DOCUMENTACAO-SIGEVI.md` — imobiliária / vistorias |
| Contexto social/profissional descrito | ⚠️ | Texto genérico; **personalizar** (estágio, familiar, nome real) |
| Quem são os usuários | ✅ | Administrador + vistoriador na documentação |
| Dificuldades atuais | ✅ | Tabela no doc (planilha, WhatsApp, etc.) |
| Como a solução melhora o processo | ✅ | Seção 1.5 do `DOCUMENTACAO-SIGEVI.md` |
| Escopo delimitado (não resolver tudo) | ✅ | Seção 3 do doc (fora de escopo listado) |

---

## 2. Levantamento e análise de requisitos

| Item do anúncio | Status | Onde / observação |
|-----------------|--------|-------------------|
| Como os requisitos foram levantados | ⚠️ | Doc cita entrevista — **confirmar se foi feita** ou ajustar texto |
| Modelo escolhido (ágil ou tradicional) | ✅ | Abordagem **ágil** (User Stories + MoSCoW) |
| User Stories + critérios de aceite | ✅ | `DOCUMENTACAO-SIGEVI.md` — seção 2.3 |
| Atores definidos | ✅ | Admin, vistoriador, sistema |
| Funcionalidades implementadas listadas | ✅ | RF01–RF08 + endpoints no `README.md` |
| Problemas do processo tratados | ✅ | Ligados às User Stories |
| Casos de uso (se abordagem tradicional) | ⚠️ | Diagrama Mermaid no doc — **exportar PNG** |
| Protótipo de tela | ❌ | Não obrigatório se o grupo escolheu abordagem ágil |

---

## 3. Desenvolvimento da solução

| Item do anúncio | Status | Onde / observação |
|-----------------|--------|-------------------|
| Back-end em Java | ✅ | Java 21 + Spring Boot 3 |
| Coerente com o problema | ✅ | Imóveis, vistorias, fotos, relatórios, auditoria |
| Frameworks livres | ✅ | Spring, JPA, JWT, Flyway, Swagger, etc. |
| **SRP** (Single Responsibility) | ✅ | Validators, services e controllers separados |
| **OCP** (Open/Closed) | ✅ | `RelatorioGeracaoStrategy` — novos PDFs sem alterar contexto |
| **LSP** (Liskov Substitution) | ✅ | Estratégias de relatório substituíveis |
| **ISP** (Interface Segregation) | ✅ | `AuditoriaListener` com contrato mínimo |
| **DIP** (Dependency Inversion) | ✅ | Repositories + injeção de dependência |
| Testes unitários | ⚠️ | 7 classes em `src/test/java/`; faltam Foto, Relatório, Auditoria |
| Padrões **criacionais** | ✅ | Factory, Builder, Singleton |
| Padrões **comportamentais** | ✅ | Strategy, Observer, Repository |
| Padrões **estruturais** | ⚠️ | DTO / Facade implícitos — **justificar na apresentação** |
| Justificativa dos padrões | ⚠️ | `README.md` + documentação; vale **1 slide por padrão** |

---

## 4. Modelagem da solução

| Item do anúncio | Status | Onde / observação |
|-----------------|--------|-------------------|
| Diagrama de classes (obrigatório) | ⚠️ | `docs/DIAGRAMA-CLASSES.md` (Mermaid) — **exportar PNG** |
| Outros diagramas suficientes | ⚠️ | Casos de uso, sequência (login), ER em `DOCUMENTACAO-SIGEVI.md` |
| Diagramas na pasta do GitHub | ❌ | Criar `docs/diagramas/*.png` |

---

## 5. Entregáveis no GitHub

| Item do anúncio | Status | Onde / observação |
|-----------------|--------|-------------------|
| Código-fonte completo | ✅ | Pasta `sigevi/` |
| Testes unitários | ✅ | `sigevi/src/test/java/` |
| Documentação do grupo | ⚠️ | `docs/DOCUMENTACAO-SIGEVI.md` — pode faltar lista de integrantes |
| Diagramas | ⚠️ | Apenas em Markdown; falta exportar imagens |
| Pelo menos um diagrama de classes | ⚠️ | Existe em Mermaid; falta PNG para entrega visual |
| Instruções de execução | ✅ | `README.md` (inclui perfil `dev` sem PostgreSQL) |
| Repositório organizado | ✅ | Estrutura em camadas clara |

---

## 6. Formação dos grupos e entrega (ULife)

| Item do anúncio | Status | Observação |
|-----------------|--------|------------|
| Grupo de no máximo 5 alunos | ❓ | Definir e documentar |
| Lista de integrantes na ULife | ❌ | Entrega na plataforma |
| URL do repositório na ULife | ❌ | https://github.com/Pedrochristovam/traballhofinal |

---

## 7. Acompanhamento do projeto

| Item do anúncio | Status | Observação |
|-----------------|--------|------------|
| Repositório atualizado ao longo das semanas | ⚠️ | Depende do histórico de commits do grupo |
| Commits de **todos** os integrantes | ❓ | Verificar em GitHub → Insights → Contributors |

---

## 8. Critérios gerais de avaliação (visão geral)

| Critério | Situação |
|----------|----------|
| Coerência e relevância do problema | ✅ Alto (após personalizar contexto) |
| Qualidade do levantamento de requisitos | ⚠️ Médio — documentado, falta evidência da elicitação |
| Organização e modelagem | ⚠️ Médio — falta PNG dos diagramas |
| Qualidade do código | ✅ Alto |
| SOLID | ✅ Alto |
| Padrões de projeto | ✅ Bom (reforçar estrutural) |
| Testes unitários | ⚠️ Médio |
| Organização do repositório | ✅ Alto |
| Evolução contínua / commits do grupo | ❓ Verificar no GitHub |
| Clareza na apresentação | ❌ Slides e ensaio pendentes |

---

## Resumo por área

| Área | Situação |
|------|----------|
| **Código back-end** | ✅ Praticamente completo |
| **SOLID + padrões** | ✅ Bom |
| **Testes** | ⚠️ Parcial |
| **Documentação acadêmica** | ⚠️ Existe em MD; falta polir e exportar diagramas |
| **Diagramas para entrega** | ❌ Exportar PNG |
| **ULife + commits do grupo** | ❌ Ação do grupo |

---

## Precisa de front-end?

**Não.** O anúncio pede explicitamente:

- *solução **back-end** em Java*
- *implementar uma **parte relevante** da solução*

Não há exigência de interface web ou mobile. Para **demonstrar o sistema** na apresentação:

| Ferramenta | Uso |
|------------|-----|
| **Swagger** | http://localhost:8080/api/swagger-ui.html |
| **Demo ao vivo** | Login → imóvel → vistoria → foto → relatório PDF |
| **Postman / Insomnia** | Opcional — mesmos endpoints da API |

Front-end seria um **diferencial**, não um requisito. O professor avalia API, modelagem, código, testes e documentação.

---

## Próximos passos (prioridade)

1. [ ] Exportar diagramas (classes, casos de uso, sequência) → `docs/diagramas/*.png`  
2. [ ] Criar `docs/INTEGRANTES.md` com nomes e RAs  
3. [ ] Personalizar contexto do problema (história real do grupo)  
4. [ ] Garantir commits de todos no GitHub  
5. [ ] Ensaiar demo com perfil `dev`:  
   ```bash
   cd sigevi
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```  
6. [ ] Montar slides (10–15 min): problema → requisitos → diagrama → SOLID/padrões → demo Swagger  
7. [ ] Entregar na ULife: integrantes + URL do repositório  

---

## Documentos relacionados no repositório

| Arquivo | Conteúdo |
|---------|----------|
| `README.md` | Execução técnica, endpoints, stack |
| `docs/DOCUMENTACAO-SIGEVI.md` | Problema, requisitos, SOLID, padrões |
| `docs/DIAGRAMA-CLASSES.md` | Diagramas de classes (Mermaid) |
| `docs/CHECKLIST-TRABALHO.md` | Este checklist |

---

*Atualizar este arquivo conforme o grupo for concluindo os itens pendentes.*
