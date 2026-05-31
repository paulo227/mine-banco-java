package model;

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

    public void depositar(double valor) {

        if (valor > 0) {

            saldo += valor;

            System.out.println("Depósito realizado.");
            System.out.println("Saldo atual: R$ " + saldo);

        } else {
            System.out.println("Valor inválido.");
        }
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public abstract void sacar(double valor);

    public void transferir(Conta destino, double valor) {

        if (valor <= saldo) {

            saldo -= valor;
            destino.saldo += valor;

            System.out.println("Transferência realizada.");

        } else {
            System.out.println("Saldo insuficiente.");
        }

    }


    public void mostrarDados() {

        System.out.println("Conta: " + numeroConta);
        System.out.println("Cliente: " + cliente.getNome());
        System.out.println("Saldo: R$ " + saldo);

    }
}
