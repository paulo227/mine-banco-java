-- ============================================================
-- MineBanco - Script de criação/atualização do banco PostgreSQL
-- ============================================================

-- Executar manualmente se necessário:
-- CREATE DATABASE minebanco;

-- -------------------------
-- Tabela de clientes
-- -------------------------
CREATE TABLE IF NOT EXISTS cliente (
    id   SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf  VARCHAR(11)  UNIQUE NOT NULL
);

-- -------------------------
-- Tabela de contas
-- -------------------------
CREATE TABLE IF NOT EXISTS conta (
    numero     INT PRIMARY KEY,
    tipo       VARCHAR(10) NOT NULL CHECK (tipo IN ('CORRENTE', 'POUPANCA')),
    saldo      DECIMAL(15,2) NOT NULL DEFAULT 0,
    limite     DECIMAL(15,2) NOT NULL DEFAULT 0,
    cliente_id INT NOT NULL REFERENCES cliente(id) ON DELETE RESTRICT,
    criado_em  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- -------------------------
-- Tabela de histórico de transações (extrato)
-- -------------------------
CREATE TABLE IF NOT EXISTS transacao (
    id               BIGSERIAL PRIMARY KEY,
    tipo             VARCHAR(20) NOT NULL CHECK (tipo IN ('DEPOSITO', 'SAQUE', 'TRANSFERENCIA', 'RENDIMENTO')),
    numero_conta     INT NOT NULL REFERENCES conta(numero) ON DELETE CASCADE,
    valor            DECIMAL(15,2) NOT NULL,
    conta_relacionada INT REFERENCES conta(numero) ON DELETE SET NULL,
    descricao        VARCHAR(255),
    criado_em        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Índice para acelerar busca de extrato por conta
CREATE INDEX IF NOT EXISTS idx_transacao_numero_conta ON transacao(numero_conta);
CREATE INDEX IF NOT EXISTS idx_transacao_criado_em    ON transacao(criado_em DESC);
