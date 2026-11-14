package controller.dto;

import jakarta.validation.constraints.Positive;

/**
 * Request para operações de depósito e saque.
 */
public class OperacaoRequest {

    @Positive(message = "O valor da operação deve ser positivo.")
    private double valor;

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
}
