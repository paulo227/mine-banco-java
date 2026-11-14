package controller.dto;

import jakarta.validation.constraints.*;

/**
 * Request para criação de nova conta bancária.
 */
public class CriarContaRequest {

    @NotBlank(message = "Nome do cliente é obrigatório.")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres.")
    private String nome;

    @NotBlank(message = "CPF é obrigatório.")
    @Size(min = 11, max = 11, message = "CPF deve ter exatamente 11 dígitos (sem pontuação).")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter apenas dígitos.")
    private String cpf;

    @Positive(message = "Número da conta deve ser positivo.")
    private int numero;

    @NotBlank(message = "Tipo da conta é obrigatório. Use 'CORRENTE' ou 'POUPANCA'.")
    @Pattern(regexp = "CORRENTE|POUPANCA", message = "Tipo deve ser 'CORRENTE' ou 'POUPANCA'.")
    private String tipo;

    @PositiveOrZero(message = "Limite não pode ser negativo.")
    private double limite;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public double getLimite() { return limite; }
    public void setLimite(double limite) { this.limite = limite; }
}
