package util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import model.Cliente;
import model.Conta;
import model.ContaCorrente;
import model.ContaPoupanca;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ContaDAO {

    private final DataSource dataSource;

    public ContaDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ContaDAO() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(getEnvOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/minebanco"));
        config.setUsername(getEnvOrDefault("DB_USER", "postgres"));
        config.setPassword(getEnvOrDefault("DB_PASSWORD", "postgres"));
        this.dataSource = new HikariDataSource(config);
    }

    private static String getEnvOrDefault(String envVar, String defaultValue) {
        String value = System.getenv(envVar);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }

    private Connection conectar() throws SQLException {
        return dataSource.getConnection();
    }

    public int salvarCliente(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente (nome, cpf) VALUES (?, ?) RETURNING id";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            throw new SQLException("Erro ao salvar cliente: nenhum ID retornado.");
        }
    }

    public Integer buscarIdClientePorCpf(String cpf) throws SQLException {
        String sql = "SELECT id FROM cliente WHERE cpf = ?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            return null;
        }
    }

    public void salvarConta(Conta conta) throws SQLException {
        String tipo;
        double limite = 0;

        if (conta instanceof ContaCorrente) {
            tipo = "CORRENTE";
            limite = ((ContaCorrente) conta).getLimite();
        } else if (conta instanceof ContaPoupanca) {
            tipo = "POUPANCA";
        } else {
            throw new IllegalArgumentException("Tipo de conta desconhecido.");
        }

        Integer clienteId = buscarIdClientePorCpf(conta.getCliente().getCpf());
        if (clienteId == null) {
            clienteId = salvarCliente(conta.getCliente());
        }

        String sql = "INSERT INTO conta (numero, tipo, saldo, limite, cliente_id) VALUES (?, ?, ?, ?, ?) " +
                     "ON CONFLICT (numero) DO UPDATE SET saldo = EXCLUDED.saldo, limite = EXCLUDED.limite";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, conta.getNumeroConta());
            stmt.setString(2, tipo);
            stmt.setDouble(3, conta.getSaldo());
            stmt.setDouble(4, limite);
            stmt.setInt(5, clienteId);
            stmt.executeUpdate();
        }
    }

    public void atualizarSaldo(int numeroConta, double novoSaldo) throws SQLException {
        String sql = "UPDATE conta SET saldo = ? WHERE numero = ?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, novoSaldo);
            stmt.setInt(2, numeroConta);
            stmt.executeUpdate();
        }
    }

    public Conta buscarConta(int numeroConta) throws SQLException {
        String sql = "SELECT c.numero, c.tipo, c.saldo, c.limite, cl.id, cl.nome, cl.cpf " +
                     "FROM conta c JOIN cliente cl ON c.cliente_id = cl.id WHERE c.numero = ?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, numeroConta);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return montarConta(rs);
            }
            return null;
        }
    }

    public List<Conta> listarContas() throws SQLException {
        String sql = "SELECT c.numero, c.tipo, c.saldo, c.limite, cl.id, cl.nome, cl.cpf " +
                     "FROM conta c JOIN cliente cl ON c.cliente_id = cl.id ORDER BY c.numero";
        List<Conta> contas = new ArrayList<>();
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                contas.add(montarConta(rs));
            }
        }
        return contas;
    }

    public void removerConta(int numeroConta) throws SQLException {
        String sql = "DELETE FROM conta WHERE numero = ?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, numeroConta);
            stmt.executeUpdate();
        }
    }

    private Conta montarConta(ResultSet rs) throws SQLException {
        int numero = rs.getInt("numero");
        double saldo = rs.getDouble("saldo");
        double limite = rs.getDouble("limite");
        String tipo = rs.getString("tipo");
        String nome = rs.getString("nome");
        String cpf = rs.getString("cpf");

        Cliente cliente = new Cliente(nome, cpf);
        Conta conta;

        if ("CORRENTE".equals(tipo)) {
            conta = new ContaCorrente(numero, cliente, limite);
        } else {
            conta = new ContaPoupanca(numero, cliente);
        }

        conta.setSaldo(saldo);
        return conta;
    }
}
