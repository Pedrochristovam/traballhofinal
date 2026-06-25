# Painel Operacional — SIGEVI

Documentação técnica da funcionalidade de visão operacional de vistorias.

## Problema identificado

O SIGEVI gerencia o ciclo completo de vistorias (cadastro, status, fotos, relatórios PDF e auditoria), porém **não oferecia uma visão consolidada** para gestores e inspetores. Para saber quantas vistorias estavam pendentes, atrasadas ou concluídas, era necessário consultar entidade por entidade (`imóvel → vistorias → status`).

Esse gap impacta diretamente a operação diária de imobiliárias:
- Administradores não conseguem priorizar vistorias atrasadas.
- Inspetores não têm visão rápida da própria carga de trabalho.
- Métricas de produtividade exigiam consultas manuais ou planilhas externas.

## Solução implementada

Módulo **Operacional** (`/api/operacional`) com endpoints read-only que agregam dados existentes, sem alteração de schema.

### Endpoints

| Método | Rota | Perfil | Descrição |
|--------|------|--------|-----------|
| GET | `/operacional/resumo` | ADMIN | KPIs globais (totais, status, atrasos, concluídas no período) |
| GET | `/operacional/minha-carga` | ADMIN, USER | Carga do inspetor autenticado |
| GET | `/operacional/vistorias` | ADMIN | Listagem filtrada por status, inspetor e período |
| GET | `/operacional/vistorias/atrasadas` | ADMIN | Vistorias AGENDADA/EM_ANDAMENTO com data anterior a hoje |
| GET | `/operacional/inspetores/produtividade` | ADMIN | Ranking de concluídas e pendentes por inspetor |
| GET | `/operacional/atividades-recentes?limite=N` | ADMIN | Feed global de auditoria (limite 1–100) |

### Regras de negócio

- **Vistoria atrasada:** `dataVistoria < hoje` e status `AGENDADA` ou `EM_ANDAMENTO`.
- **Concluídas no período:** status `CONCLUIDA` com `atualizadoEm` dentro da semana/mês corrente.
- **Validações:** `dataInicio <= dataFim`; `limite` entre 1 e 100 (retorna HTTP 422).

### Frontend

O painel demo (`app.html`) exibe automaticamente:
- **ADMIN:** KPIs, tabela de atrasadas, produtividade e atividades recentes.
- **USER:** KPIs da carga pessoal e próximas vistorias.

## Arquivos criados

- `controller/OperacionalController.java`
- `service/OperacionalService.java`
- `mapper/OperacionalMapper.java`
- `dto/response/ResumoOperacionalResponse.java`
- `dto/response/MinhaCargaResponse.java`
- `dto/response/VistoriaOperacionalResponse.java`
- `dto/response/InspetorProdutividadeResponse.java`
- `test/.../OperacionalServiceTest.java`
- `docs/PAINEL-OPERACIONAL.md` (este arquivo)

## Arquivos modificados

- `repository/VistoriaRepository.java` — queries agregadas e filtros
- `repository/AuditoriaRepository.java` — atividades recentes paginadas
- `static/app.html`, `static/app.js`, `static/styles.css` — UI do painel

## Impactos no sistema

- **Positivos:** melhora produtividade operacional sem novas tabelas ou migrações.
- **Performance:** consultas agregadas; recomendável monitorar volume em produção com muitos registros (futuro: paginação).
- **Segurança:** endpoints administrativos protegidos com `@PreAuthorize`; inspetores veem apenas a própria carga.
- **Compatibilidade:** nenhuma breaking change em APIs existentes.

## Como testar

### 1. Subir a aplicação

```bash
cd sigevi
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Acesse `http://localhost:8080/api/index.html` e faça login (`admin@sigevi.com` / `Admin@123`).

### 2. Painel web

Após login, o bloco **Painel operacional** aparece no topo de `app.html`. Use **Atualizar** após criar vistorias ou alterar status.

### 3. API (Swagger ou curl)

```bash
# Login
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@sigevi.com","senha":"Admin@123"}' | jq -r .token)

# Resumo operacional
curl -s http://localhost:8080/api/operacional/resumo \
  -H "Authorization: Bearer $TOKEN"

# Vistorias atrasadas
curl -s http://localhost:8080/api/operacional/vistorias/atrasadas \
  -H "Authorization: Bearer $TOKEN"
```

### 4. Testes automatizados

```bash
cd sigevi
./mvnw test
```

Verifique `OperacionalServiceTest` (resumo, validação de período, limite e carga do inspetor).

### Cenário sugerido

1. Cadastre um imóvel e crie vistoria com data no passado (status AGENDADA).
2. Abra o painel — contador de **Atrasadas** deve incrementar.
3. Altere status para EM_ANDAMENTO e depois CONCLUIDA.
4. Confira incremento em **Concluídas (semana)** e redução de pendentes.
