package model;

public class ContaCorrente extends Conta implements Tributavel {

    private double limite;
    private static final double TAXA_SAQUE = 2.00;

    public ContaCorrente(int numeroConta, Cliente cliente, double limite) {
        super(numeroConta, cliente);
        this.limite = limite;
    }

    public double getLimite() {
        return limite;
    }

    public void setLimite(double limite) {
        this.limite = limite;
    }

    @Override
    public void sacar(double valor) {
        double valorTotal = valor + TAXA_SAQUE;

        if (valorTotal <= (saldo + limite)) {
            saldo -= valorTotal;
            System.out.println("Saque realizado (taxa de R$ " + TAXA_SAQUE + " cobrada).");
            System.out.println("Saldo atual: R$ " + saldo);
        } else {
            System.out.println("Saldo e limite insuficientes.");
        }
    }

    @Override
    public double calcularImposto() {
        // Cobra 1% do saldo positivo como imposto
        if (saldo > 0) {
            return saldo * 0.01;
        }
        return 0.0;
    }

    @Override
    public void mostrarDados() {
        System.out.println("Tipo: Conta Corrente");
        super.mostrarDados();
        System.out.println("Limite: R$ " + limite);
        System.out.println("Imposto Estimado (1%): R$ " + calcularImposto());
    }
}
