package model;

import exception.SaldoInsuficienteException;

/**
 * Conta Corrente com limite de crédito e taxa de saque.
 * Implementa Tributavel: paga 1% de imposto sobre saldo positivo.
 */
public class ContaCorrente extends Conta implements Tributavel {

    private double limite;
    private static final double TAXA_SAQUE = 2.00;

    public ContaCorrente(int numeroConta, Cliente cliente, double limite) {
        super(numeroConta, cliente);
        if (limite < 0) {
            throw new IllegalArgumentException("Limite não pode ser negativo.");
        }
        this.limite = limite;
    }

    public double getLimite() {
        return limite;
    }

    public void setLimite(double limite) {
        if (limite < 0) {
            throw new IllegalArgumentException("Limite não pode ser negativo.");
        }
        this.limite = limite;
    }

    /**
     * Realiza saque cobrando a taxa fixa de R$ 2,00.
     * Permite usar o limite de crédito caso o saldo seja insuficiente.
     *
     * @param valor valor a sacar (sem a taxa)
     * @throws SaldoInsuficienteException se saldo + limite não cobrirem valor + taxa
     */
    @Override
    public void sacar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor de saque deve ser positivo.");
        }
        double valorTotal = valor + TAXA_SAQUE;
        if (valorTotal > (saldo + limite)) {
            throw new SaldoInsuficienteException(
                "Saldo e limite insuficientes. Disponível: R$ " + String.format("%.2f", saldo + limite) +
                ". Necessário (com taxa): R$ " + String.format("%.2f", valorTotal));
        }
        saldo -= valorTotal;
    }

    /**
     * Calcula 1% do saldo positivo como imposto de renda estimado.
     */
    @Override
    public double calcularImposto() {
        return saldo > 0 ? saldo * 0.01 : 0.0;
    }

    @Override
    public String mostrarDados() {
        return super.mostrarDados() +
               " | Tipo: Corrente | Limite: R$ " + String.format("%.2f", limite) +
               " | Imposto estimado: R$ " + String.format("%.2f", calcularImposto());
    }
}
