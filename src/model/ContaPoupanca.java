package model;

import exception.SaldoInsuficienteException;

/**
 * Conta Poupança: não possui limite de crédito, não paga imposto.
 * Suporta aplicação de rendimento sobre o saldo.
 */
public class ContaPoupanca extends Conta {

    public ContaPoupanca(int numeroConta, Cliente cliente) {
        super(numeroConta, cliente);
    }

    /**
     * Realiza saque — não permite saldo negativo.
     *
     * @param valor valor a sacar
     * @throws SaldoInsuficienteException se o saldo for insuficiente
     */
    @Override
    public void sacar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor de saque deve ser positivo.");
        }
        if (valor > saldo) {
            throw new SaldoInsuficienteException(
                "Saldo insuficiente na Poupança. Saldo atual: R$ " + String.format("%.2f", saldo));
        }
        saldo -= valor;
    }

    /**
     * Aplica rendimento percentual ao saldo atual.
     *
     * @param taxa percentual em decimal (ex: 0.015 = 1.5%)
     */
    public void aplicarRendimento(double taxa) {
        if (taxa <= 0) {
            throw new IllegalArgumentException("Taxa de rendimento deve ser positiva. Taxa informada: " + taxa);
        }
        double rendimento = saldo * taxa;
        saldo += rendimento;
    }

    @Override
    public String mostrarDados() {
        return super.mostrarDados() + " | Tipo: Poupança";
    }
}
