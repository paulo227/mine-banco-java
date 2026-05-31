package util;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class DatabaseManager {

    private final DataSource dataSource;

    public DatabaseManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection conectar() throws SQLException {
        return dataSource.getConnection();
    }

    @PostConstruct
    public void inicializarTabelas() {
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
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabelas verificadas/criadas com sucesso.");
        } catch (SQLException e) {
            System.err.println("Erro ao inicializar tabelas: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
