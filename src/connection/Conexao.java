package connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class Conexao {

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

    private Conexao() {}

    private static String getEnvOrDefault(String envVar, String defaultValue) {
        String value = System.getenv(envVar);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }

    public static HikariDataSource getDataSource() {
        return dataSource;
    }

    public static void conectar() {
        try {
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
            try (var conn = dataSource.getConnection();
                 var stmt = conn.createStatement()) {
                stmt.execute(sql);
            }
            System.out.println("Conectado ao banco de dados!");
        } catch (Exception e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
