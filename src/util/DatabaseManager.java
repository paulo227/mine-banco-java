package util;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Inicializa as tabelas do banco de dados ao subir a aplicação,
 * caso ainda não existam (idempotente via IF NOT EXISTS).
 */
@Component
public class DatabaseManager {

    private final DataSource dataSource;

    public DatabaseManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void inicializarTabelas() {
        String sql = """
            CREATE TABLE IF NOT EXISTS cliente (
                id   SERIAL PRIMARY KEY,
                nome VARCHAR(100) NOT NULL,
                cpf  VARCHAR(11)  UNIQUE NOT NULL
            );
            
            CREATE TABLE IF NOT EXISTS conta (
                numero     INT PRIMARY KEY,
                tipo       VARCHAR(10) NOT NULL CHECK (tipo IN ('CORRENTE', 'POUPANCA')),
                saldo      DECIMAL(15,2) NOT NULL DEFAULT 0,
                limite     DECIMAL(15,2) NOT NULL DEFAULT 0,
                cliente_id INT NOT NULL REFERENCES cliente(id) ON DELETE RESTRICT,
                criado_em  TIMESTAMPTZ NOT NULL DEFAULT NOW()
            );
            
            CREATE TABLE IF NOT EXISTS transacao (
                id                BIGSERIAL PRIMARY KEY,
                tipo              VARCHAR(20) NOT NULL CHECK (tipo IN ('DEPOSITO', 'SAQUE', 'TRANSFERENCIA', 'RENDIMENTO')),
                numero_conta      INT NOT NULL REFERENCES conta(numero) ON DELETE CASCADE,
                valor             DECIMAL(15,2) NOT NULL,
                conta_relacionada INT REFERENCES conta(numero) ON DELETE SET NULL,
                descricao         VARCHAR(255),
                criado_em         TIMESTAMPTZ NOT NULL DEFAULT NOW()
            );
            
            CREATE INDEX IF NOT EXISTS idx_transacao_numero_conta ON transacao(numero_conta);
            CREATE INDEX IF NOT EXISTS idx_transacao_criado_em    ON transacao(criado_em DESC);
            """;

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inicializar tabelas do banco: " + e.getMessage(), e);
        }
    }
}
