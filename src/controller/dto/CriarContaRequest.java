package controller.dto;

public class CriarContaRequest {
    private String nome;
    private String cpf;
    private int numero;
    private int tipo;
    private double limite;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }
    public int getTipo() { return tipo; }
    public void setTipo(int tipo) { this.tipo = tipo; }
    public double getLimite() { return limite; }
    public void setLimite(double limite) { this.limite = limite; }
}
