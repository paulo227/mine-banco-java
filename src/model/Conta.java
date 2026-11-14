package model;

import exception.SaldoInsuficienteException;

/**
 * Classe abstrata que representa uma conta bancária genérica.
 * Contém as operações comuns a todos os tipos de conta.
 */
public abstract class Conta {

    private int numeroConta;
    protected double saldo;
    private Cliente cliente;

    public Conta(int numeroConta, Cliente cliente) {
        this.numeroConta = numeroConta;
        this.cliente = cliente;
        this.saldo = 0;
    }

    public int getNumeroConta() {
        return numeroConta;
    }

    public double getSaldo() {
        return saldo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    /**
     * Deposita um valor na conta.
     * @param valor deve ser positivo
     * @throws IllegalArgumentException se o valor for inválido
     */
    public void depositar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor de depósito deve ser positivo. Valor informado: " + valor);
        }
        saldo += valor;
    }

    /**
     * Realiza saque da conta. Implementação varia por tipo de conta.
     * @param valor deve ser positivo
     */
    public abstract void sacar(double valor);

    /**
     * Transfere um valor desta conta para outra.
     * @param destino conta de destino
     * @param valor   valor a transferir
     * @throws SaldoInsuficienteException se não houver saldo disponível
     */
    public void transferir(Conta destino, double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor de transferência deve ser positivo.");
        }
        if (valor > saldo) {
            throw new SaldoInsuficienteException("Saldo insuficiente para transferência. Saldo: R$ " + saldo);
        }
        saldo -= valor;
        destino.saldo += valor;
    }

    /**
     * Retorna uma representação em texto dos dados da conta para logs/console.
     */
    public String mostrarDados() {
        return "Conta: " + numeroConta +
               " | Cliente: " + cliente.getNome() +
               " | Saldo: R$ " + String.format("%.2f", saldo);
    }
}
