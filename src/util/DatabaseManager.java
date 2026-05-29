package util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {

    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(getEnvOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/minebanco"));
        config.setUsername(getEnvOrDefault("DB_USER", "postgres"));
        config.setPassword(getEnvOrDefault("DB_PASSWORD", "postgres"));
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        dataSource = new HikariDataSource(config);
    }

    private static String getEnvOrDefault(String envVar, String defaultValue) {
        String value = System.getenv(envVar);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }

    public static Connection conectar() throws SQLException {
        return dataSource.getConnection();
    }

    public static void fecharConexao(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }

    public static void fecharPool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public static void inicializarTabelas() {
        String sql = "CREATE TABLE IF NOT EXISTS cliente (" +
                "id SERIAL PRIMARY KEY, " +
                "nome VARCHAR(100) NOT NULL, " +
                "cpf VARCHAR(11) UNIQUE NOT NULL" +
                "); " +
                "CREATE TABLE IF NOT EXISTS conta (" +
                "numero INT PRIMARY KEY, " +
                "tipo VARCHAR(10) NOT NULL CHECK (tipo IN ('CORRENTE', 'POUPANCA')), " +
                "saldo DECIMAL(15,2) NOT NULL DEFAULT 0, " +
                "limite DECIMAL(15,2) NOT NULL DEFAULT 0, " +
                "cliente_id INT NOT NULL REFERENCES cliente(id)" +
                ");";
        try (Connection conn = conectar();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabelas verificadas/criadas com sucesso.");
        } catch (SQLException e) {
            System.err.println("Erro ao inicializar tabelas: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
