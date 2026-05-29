-- ============================================
-- Script de criação do banco de dados PostgreSQL
-- para o projeto MineBanco
-- ============================================

-- Criação do banco (executar manualmente se necessário)
-- CREATE DATABASE minebanco;

-- Tabela de clientes
CREATE TABLE IF NOT EXISTS cliente (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(11) UNIQUE NOT NULL
);

-- Tabela de contas
CREATE TABLE IF NOT EXISTS conta (
    numero INT PRIMARY KEY,
    tipo VARCHAR(10) NOT NULL CHECK (tipo IN ('CORRENTE', 'POUPANCA')),
    saldo DECIMAL(15,2) NOT NULL DEFAULT 0,
    limite DECIMAL(15,2) NOT NULL DEFAULT 0,
    cliente_id INT NOT NULL REFERENCES cliente(id)
);
