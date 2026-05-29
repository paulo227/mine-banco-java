package model;

public class ContaPoupanca extends Conta {

    public ContaPoupanca(int numeroConta, Cliente cliente) {
        super(numeroConta, cliente);
    }

    @Override
    public void sacar(double valor) {
        if (valor <= saldo) {
            saldo -= valor;
            System.out.println("Saque realizado.");
            System.out.println("Saldo atual: R$ " + saldo);
        } else {
            System.out.println("Saldo insuficiente na Poupança.");
        }
    }

    public void aplicarRendimento(double taxa) {
        if (taxa > 0) {
            double rendimento = saldo * taxa;
            saldo += rendimento;
            System.out.println("Rendimento de R$ " + rendimento + " aplicado (Taxa: " + (taxa * 100) + "%).");
            System.out.println("Novo saldo: R$ " + saldo);
        } else {
            System.out.println("Taxa de rendimento inválida.");
        }
    }

    @Override
    public void mostrarDados() {
        System.out.println("Tipo: Conta Poupança");
        super.mostrarDados();
    }
}
