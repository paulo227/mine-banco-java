package util;

import exception.ContaNaoEncontradaException;
import model.Cliente;
import model.Conta;
import model.ContaCorrente;
import model.ContaPoupanca;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsável por todas as operações de persistência de Conta e Cliente.
 * Utiliza o DataSource gerenciado pelo Spring (HikariCP) — sem construtor manual.
 */
@Repository
public class ContaDAO {

    private final DataSource dataSource;

    public ContaDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // -------------------------------------------------------------------------
    // Cliente
    // -------------------------------------------------------------------------

    public int salvarCliente(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente (nome, cpf) VALUES (?, ?) RETURNING id";
        try (Connection conn = dataSource.getConnection();
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
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            return null;
        }
    }

    // -------------------------------------------------------------------------
    // Conta — operações simples
    // -------------------------------------------------------------------------

    public void salvarConta(Conta conta) throws SQLException {
        String tipo;
        double limite = 0;

        if (conta instanceof ContaCorrente cc) {
            tipo = "CORRENTE";
            limite = cc.getLimite();
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
        try (Connection conn = dataSource.getConnection();
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
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, novoSaldo);
            stmt.setInt(2, numeroConta);
            stmt.executeUpdate();
        }
    }

    /**
     * Transferência atômica: debita a origem e credita o destino na mesma transação SQL.
     * Se qualquer UPDATE falhar, o rollback é automático.
     *
     * @param numeroOrigem  número da conta de origem
     * @param saldoOrigem   novo saldo da conta de origem após a transferência
     * @param numeroDestino número da conta de destino
     * @param saldoDestino  novo saldo da conta de destino após a transferência
     */
    public void transferirAtomico(int numeroOrigem, double saldoOrigem,
                                  int numeroDestino, double saldoDestino) throws SQLException {
        String sql = "UPDATE conta SET saldo = ? WHERE numero = ?";
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Debitar origem
                stmt.setDouble(1, saldoOrigem);
                stmt.setInt(2, numeroOrigem);
                stmt.executeUpdate();

                // Creditar destino
                stmt.setDouble(1, saldoDestino);
                stmt.setInt(2, numeroDestino);
                stmt.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public Conta buscarConta(int numeroConta) throws SQLException {
        String sql = "SELECT c.numero, c.tipo, c.saldo, c.limite, cl.id, cl.nome, cl.cpf " +
                     "FROM conta c JOIN cliente cl ON c.cliente_id = cl.id WHERE c.numero = ?";
        try (Connection conn = dataSource.getConnection();
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
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                contas.add(montarConta(rs));
            }
        }
        return contas;
    }

    /**
     * Lista contas com paginação para evitar problemas de memória em grandes volumes.
     */
    public List<Conta> listarContasPaginado(int page, int size) throws SQLException {
        String sql = "SELECT c.numero, c.tipo, c.saldo, c.limite, cl.id, cl.nome, cl.cpf " +
                     "FROM conta c JOIN cliente cl ON c.cliente_id = cl.id ORDER BY c.numero LIMIT ? OFFSET ?";
        List<Conta> contas = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, size);
            stmt.setInt(2, page * size);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    contas.add(montarConta(rs));
                }
            }
        }
        return contas;
    }

    public long contarContas() throws SQLException {
        String sql = "SELECT COUNT(*) FROM conta";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
            return 0;
        }
    }

    public void removerConta(int numeroConta) throws SQLException {
        String sql = "DELETE FROM conta WHERE numero = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, numeroConta);
            stmt.executeUpdate();
        }
    }

    // -------------------------------------------------------------------------
    // Histórico de transações
    // -------------------------------------------------------------------------

    /**
     * Registra uma transação no histórico.
     *
     * @param tipo         DEPOSITO | SAQUE | TRANSFERENCIA | RENDIMENTO
     * @param numeroConta  conta principal envolvida
     * @param valor        valor da operação
     * @param contaRelacionada número da conta relacionada (ou null)
     * @param descricao    descrição adicional
     */
    public void registrarTransacao(String tipo, int numeroConta, double valor,
                                   Integer contaRelacionada, String descricao) throws SQLException {
        String sql = "INSERT INTO transacao (tipo, numero_conta, valor, conta_relacionada, descricao) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tipo);
            stmt.setInt(2, numeroConta);
            stmt.setDouble(3, valor);
            if (contaRelacionada != null) {
                stmt.setInt(4, contaRelacionada);
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setString(5, descricao);
            stmt.executeUpdate();
        }
    }

    public List<model.Transacao> buscarExtrato(int numeroConta) throws SQLException {
        String sql = "SELECT id, tipo, numero_conta, valor, conta_relacionada, descricao, criado_em " +
                     "FROM transacao WHERE numero_conta = ? ORDER BY criado_em DESC";
        List<model.Transacao> lista = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, numeroConta);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new model.Transacao(
                        rs.getLong("id"),
                        rs.getString("tipo"),
                        rs.getInt("numero_conta"),
                        rs.getDouble("valor"),
                        rs.getObject("conta_relacionada") != null ? rs.getInt("conta_relacionada") : null,
                        rs.getString("descricao"),
                        rs.getTimestamp("criado_em").toLocalDateTime()
                    ));
                }
            }
        }
        return lista;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

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
