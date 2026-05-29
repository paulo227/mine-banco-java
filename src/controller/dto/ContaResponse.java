package controller.dto;

public class ContaResponse {
    private int numero;
    private String tipo;
    private double saldo;
    private double limite;
    private String clienteNome;
    private String clienteCpf;
    private double imposto;

    public ContaResponse() {}

    public ContaResponse(int numero, String tipo, double saldo, double limite,
                         String clienteNome, String clienteCpf, double imposto) {
        this.numero = numero;
        this.tipo = tipo;
        this.saldo = saldo;
        this.limite = limite;
        this.clienteNome = clienteNome;
        this.clienteCpf = clienteCpf;
        this.imposto = imposto;
    }

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }
    public double getLimite() { return limite; }
    public void setLimite(double limite) { this.limite = limite; }
    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }
    public String getClienteCpf() { return clienteCpf; }
    public void setClienteCpf(String clienteCpf) { this.clienteCpf = clienteCpf; }
    public double getImposto() { return imposto; }
    public void setImposto(double imposto) { this.imposto = imposto; }
}
