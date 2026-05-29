package service;

import model.Conta;
import model.ContaPoupanca;
import model.Tributavel;
import util.ContaDAO;
import util.Notificador;
import util.NotificadorConsole;

import java.sql.SQLException;
import java.util.List;

public class Banco {

    private final ContaDAO contaDAO = new ContaDAO();
    private Notificador notificador = new NotificadorConsole();

    public void setNotificador(Notificador notificador) {
        this.notificador = notificador;
    }

    public void criarConta(Conta conta) {
        try {
            contaDAO.salvarConta(conta);
            notificador.notificar("Conta criada com sucesso.");
        } catch (SQLException e) {
            notificador.notificar("Erro ao criar conta: " + e.getMessage());
        }
    }

    public Conta buscarConta(int numeroConta) {
        try {
            return contaDAO.buscarConta(numeroConta);
        } catch (SQLException e) {
            notificador.notificar("Erro ao buscar conta: " + e.getMessage());
            return null;
        }
    }

    public void listarContas() {
        try {
            List<Conta> contas = contaDAO.listarContas();
            if (contas.isEmpty()) {
                notificador.notificar("Nenhuma conta cadastrada.");
                return;
            }
            for (Conta conta : contas) {
                conta.mostrarDados();
                System.out.println("---------------------");
            }
        } catch (SQLException e) {
            notificador.notificar("Erro ao listar contas: " + e.getMessage());
        }
    }

    public void removerConta(int numeroConta) throws SQLException {
        Conta conta = contaDAO.buscarConta(numeroConta);
        if (conta == null) {
            throw new SQLException("Conta não encontrada.");
        }
        if (conta.getSaldo() != 0) {
            throw new SQLException("Não é possível excluir conta com saldo diferente de zero.");
        }
        contaDAO.removerConta(numeroConta);
        notificador.notificar("Conta removida.");
    }

    public int totalContas() {
        try {
            return contaDAO.listarContas().size();
        } catch (SQLException e) {
            notificador.notificar("Erro ao contar contas: " + e.getMessage());
            return 0;
        }
    }

    public void depositar(int numeroConta, double valor) {
        try {
            Conta conta = contaDAO.buscarConta(numeroConta);
            if (conta != null) {
                conta.depositar(valor);
                contaDAO.atualizarSaldo(numeroConta, conta.getSaldo());
            } else {
                notificador.notificar("Conta não encontrada.");
            }
        } catch (SQLException e) {
            notificador.notificar("Erro ao depositar: " + e.getMessage());
        }
    }

    public void sacar(int numeroConta, double valor) {
        try {
            Conta conta = contaDAO.buscarConta(numeroConta);
            if (conta != null) {
                conta.sacar(valor);
                contaDAO.atualizarSaldo(numeroConta, conta.getSaldo());
            } else {
                notificador.notificar("Conta não encontrada.");
            }
        } catch (SQLException e) {
            notificador.notificar("Erro ao sacar: " + e.getMessage());
        }
    }

    public void transferir(int origem, int destino, double valor) {
        try {
            Conta contaOrigem = contaDAO.buscarConta(origem);
            Conta contaDestino = contaDAO.buscarConta(destino);
            if (contaOrigem != null && contaDestino != null) {
                contaOrigem.transferir(contaDestino, valor);
                contaDAO.atualizarSaldo(origem, contaOrigem.getSaldo());
                contaDAO.atualizarSaldo(destino, contaDestino.getSaldo());
            } else {
                notificador.notificar("Conta não encontrada.");
            }
        } catch (SQLException e) {
            notificador.notificar("Erro ao transferir: " + e.getMessage());
        }
    }

    public void aplicarRendimentoPoupancas(double taxa) {
        try {
            List<Conta> contas = contaDAO.listarContas();
            boolean encontrouPoupanca = false;
            for (Conta conta : contas) {
                if (conta instanceof ContaPoupanca) {
                    ((ContaPoupanca) conta).aplicarRendimento(taxa);
                    contaDAO.atualizarSaldo(conta.getNumeroConta(), conta.getSaldo());
                    encontrouPoupanca = true;
                }
            }
            if (!encontrouPoupanca) {
                notificador.notificar("Nenhuma conta poupança encontrada para render.");
            }
        } catch (SQLException e) {
            notificador.notificar("Erro ao aplicar rendimento: " + e.getMessage());
        }
    }

    public double calcularTotalImpostos() {
        try {
            List<Conta> contas = contaDAO.listarContas();
            double totalImpostos = 0;
            for (Conta conta : contas) {
                if (conta instanceof Tributavel) {
                    totalImpostos += ((Tributavel) conta).calcularImposto();
                }
            }
            return totalImpostos;
        } catch (SQLException e) {
            notificador.notificar("Erro ao calcular impostos: " + e.getMessage());
            return 0;
        }
    }
}