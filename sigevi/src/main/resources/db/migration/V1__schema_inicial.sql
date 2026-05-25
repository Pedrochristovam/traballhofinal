-- SIGEVI - Schema inicial PostgreSQL

CREATE TABLE usuarios (
    id              BIGSERIAL PRIMARY KEY,
    nome            VARCHAR(150) NOT NULL,
    email           VARCHAR(180) NOT NULL UNIQUE,
    senha           VARCHAR(255) NOT NULL,
    role            VARCHAR(20)  NOT NULL DEFAULT 'USER',
    ativo           BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE imoveis (
    id              BIGSERIAL PRIMARY KEY,
    matricula       VARCHAR(50)  NOT NULL UNIQUE,
    endereco        VARCHAR(300) NOT NULL,
    cidade          VARCHAR(100) NOT NULL,
    estado          VARCHAR(2)   NOT NULL,
    cep             VARCHAR(10)  NOT NULL,
    tipo            VARCHAR(30)  NOT NULL,
    area_m2         DECIMAL(10,2),
    descricao       TEXT,
    criado_em       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE vistorias (
    id              BIGSERIAL PRIMARY KEY,
    imovel_id       BIGINT       NOT NULL REFERENCES imoveis(id),
    inspetor_id     BIGINT       NOT NULL REFERENCES usuarios(id),
    status          VARCHAR(30)  NOT NULL DEFAULT 'AGENDADA',
    data_vistoria   DATE         NOT NULL,
    observacoes     TEXT,
    criado_em       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE fotos (
    id              BIGSERIAL PRIMARY KEY,
    vistoria_id     BIGINT       NOT NULL REFERENCES vistorias(id) ON DELETE CASCADE,
    nome_arquivo    VARCHAR(255) NOT NULL,
    caminho         VARCHAR(500) NOT NULL,
    content_type    VARCHAR(100) NOT NULL,
    tamanho_bytes   BIGINT       NOT NULL,
    criado_em       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE relatorios (
    id              BIGSERIAL PRIMARY KEY,
    vistoria_id     BIGINT       NOT NULL REFERENCES vistorias(id),
    tipo            VARCHAR(30)  NOT NULL,
    caminho_arquivo VARCHAR(500) NOT NULL,
    gerado_por_id   BIGINT       NOT NULL REFERENCES usuarios(id),
    criado_em       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE auditorias (
    id              BIGSERIAL PRIMARY KEY,
    entidade        VARCHAR(80)  NOT NULL,
    entidade_id     BIGINT       NOT NULL,
    acao            VARCHAR(30)  NOT NULL,
    valor_anterior  TEXT,
    valor_novo      TEXT,
    usuario_id      BIGINT       REFERENCES usuarios(id),
    criado_em       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_imoveis_matricula ON imoveis(matricula);
CREATE INDEX idx_imoveis_endereco ON imoveis(endereco);
CREATE INDEX idx_vistorias_imovel ON vistorias(imovel_id);
CREATE INDEX idx_vistorias_status ON vistorias(status);
CREATE INDEX idx_auditorias_entidade ON auditorias(entidade, entidade_id);
CREATE INDEX idx_fotos_vistoria ON fotos(vistoria_id);
