package service;

import exception.ContaNaoEncontradaException;
import exception.RegraDeNegocioException;
import model.Conta;
import model.ContaPoupanca;
import model.Tributavel;
import org.springframework.stereotype.Service;
import util.ContaDAO;
import util.Notificador;
import util.NotificadorConsole;

import java.sql.SQLException;
import java.util.List;

/**
 * Serviço principal do MineBanco.
 * Concentra as regras de negócio bancárias e coordena o acesso ao ContaDAO.
 * Exceções de domínio (ContaNaoEncontradaException, SaldoInsuficienteException, etc.)
 * são propagadas para o controller que decide o status HTTP adequado.
 */
@Service
public class Banco {

    private final ContaDAO contaDAO;
    private Notificador notificador;

    public Banco(ContaDAO contaDAO) {
        this.contaDAO = contaDAO;
        this.notificador = new NotificadorConsole();
    }

    public void setNotificador(Notificador notificador) {
        this.notificador = notificador;
    }

    // -------------------------------------------------------------------------
    // CRUD de contas
    // -------------------------------------------------------------------------

    public void criarConta(Conta conta) {
        try {
            contaDAO.salvarConta(conta);
            notificador.notificar("Conta " + conta.getNumeroConta() + " criada com sucesso.");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar conta: " + e.getMessage(), e);
        }
    }

    public Conta buscarConta(int numeroConta) {
        try {
            Conta conta = contaDAO.buscarConta(numeroConta);
            if (conta == null) {
                throw new ContaNaoEncontradaException(numeroConta);
            }
            return conta;
        } catch (ContaNaoEncontradaException e) {
            throw e;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar conta: " + e.getMessage(), e);
        }
    }

    public List<Conta> listarContas() {
        try {
            return contaDAO.listarContas();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar contas: " + e.getMessage(), e);
        }
    }

    public List<Conta> listarContasPaginado(int page, int size) {
        try {
            return contaDAO.listarContasPaginado(page, size);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar contas: " + e.getMessage(), e);
        }
    }

    public long totalContas() {
        try {
            return contaDAO.contarContas();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar contas: " + e.getMessage(), e);
        }
    }

    public void removerConta(int numeroConta) {
        try {
            Conta conta = contaDAO.buscarConta(numeroConta);
            if (conta == null) {
                throw new ContaNaoEncontradaException(numeroConta);
            }
            if (conta.getSaldo() != 0) {
                throw new RegraDeNegocioException(
                    "Não é possível excluir conta com saldo diferente de zero. Saldo atual: R$ " +
                    String.format("%.2f", conta.getSaldo()));
            }
            contaDAO.removerConta(numeroConta);
            notificador.notificar("Conta " + numeroConta + " removida.");
        } catch (ContaNaoEncontradaException | RegraDeNegocioException e) {
            throw e;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover conta: " + e.getMessage(), e);
        }
    }

    // -------------------------------------------------------------------------
    // Operações financeiras
    // -------------------------------------------------------------------------

    public void depositar(int numeroConta, double valor) {
        try {
            Conta conta = contaDAO.buscarConta(numeroConta);
            if (conta == null) throw new ContaNaoEncontradaException(numeroConta);
            conta.depositar(valor);  // lança IllegalArgumentException se inválido
            contaDAO.atualizarSaldo(numeroConta, conta.getSaldo());
            contaDAO.registrarTransacao("DEPOSITO", numeroConta, valor, null,
                "Depósito de R$ " + String.format("%.2f", valor));
            notificador.notificar("Depósito de R$ " + valor + " realizado na conta " + numeroConta);
        } catch (ContaNaoEncontradaException | IllegalArgumentException e) {
            throw e;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao depositar: " + e.getMessage(), e);
        }
    }

    public void sacar(int numeroConta, double valor) {
        try {
            Conta conta = contaDAO.buscarConta(numeroConta);
            if (conta == null) throw new ContaNaoEncontradaException(numeroConta);
            conta.sacar(valor);  // lança SaldoInsuficienteException se sem fundos
            contaDAO.atualizarSaldo(numeroConta, conta.getSaldo());
            contaDAO.registrarTransacao("SAQUE", numeroConta, valor, null,
                "Saque de R$ " + String.format("%.2f", valor));
            notificador.notificar("Saque de R$ " + valor + " realizado na conta " + numeroConta);
        } catch (ContaNaoEncontradaException | IllegalArgumentException e) {
            throw e;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao sacar: " + e.getMessage(), e);
        }
    }

    /**
     * Transferência ATÔMICA: usa uma única transação SQL para garantir
     * que o débito e o crédito ocorram juntos ou não ocorram.
     */
    public void transferir(int origem, int destino, double valor) {
        try {
            Conta contaOrigem = contaDAO.buscarConta(origem);
            if (contaOrigem == null) throw new ContaNaoEncontradaException(origem);

            Conta contaDestino = contaDAO.buscarConta(destino);
            if (contaDestino == null) throw new ContaNaoEncontradaException(destino);

            // A lógica de negócio (validação de saldo) ocorre no model
            contaOrigem.transferir(contaDestino, valor);

            // Persistência em uma única transação SQL atômica
            contaDAO.transferirAtomico(origem, contaOrigem.getSaldo(), destino, contaDestino.getSaldo());

            // Registrar no histórico de ambas as contas
            contaDAO.registrarTransacao("TRANSFERENCIA", origem, valor, destino,
                "Transferência de R$ " + String.format("%.2f", valor) + " para conta " + destino);
            contaDAO.registrarTransacao("TRANSFERENCIA", destino, valor, origem,
                "Transferência recebida de R$ " + String.format("%.2f", valor) + " da conta " + origem);

            notificador.notificar("Transferência de R$ " + valor + " de " + origem + " para " + destino + " concluída.");
        } catch (ContaNaoEncontradaException | IllegalArgumentException e) {
            throw e;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao transferir: " + e.getMessage(), e);
        }
    }

    public void aplicarRendimentoPoupancas(double taxa) {
        try {
            List<Conta> contas = contaDAO.listarContas();
            int count = 0;
            for (Conta conta : contas) {
                if (conta instanceof ContaPoupanca cp) {
                    cp.aplicarRendimento(taxa);
                    contaDAO.atualizarSaldo(conta.getNumeroConta(), cp.getSaldo());
                    double rendimento = cp.getSaldo() - (cp.getSaldo() / (1 + taxa));
                    contaDAO.registrarTransacao("RENDIMENTO", conta.getNumeroConta(), rendimento, null,
                        "Rendimento de " + String.format("%.2f", taxa * 100) + "% aplicado");
                    count++;
                }
            }
            if (count == 0) {
                notificador.notificar("Nenhuma conta poupança encontrada para render.");
            } else {
                notificador.notificar("Rendimento aplicado em " + count + " conta(s) poupança.");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao aplicar rendimento: " + e.getMessage(), e);
        }
    }

    public double calcularTotalImpostos() {
        try {
            List<Conta> contas = contaDAO.listarContas();
            return contas.stream()
                .filter(c -> c instanceof Tributavel)
                .mapToDouble(c -> ((Tributavel) c).calcularImposto())
                .sum();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao calcular impostos: " + e.getMessage(), e);
        }
    }

    public List<model.Transacao> buscarExtrato(int numeroConta) {
        try {
            Conta conta = contaDAO.buscarConta(numeroConta);
            if (conta == null) throw new ContaNaoEncontradaException(numeroConta);
            return contaDAO.buscarExtrato(numeroConta);
        } catch (ContaNaoEncontradaException e) {
            throw e;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar extrato: " + e.getMessage(), e);
        }
    }
}
