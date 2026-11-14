package controller.dto;

import jakarta.validation.constraints.Positive;

/**
 * Request para operações de transferência entre contas.
 */
public class TransferenciaRequest {

    @Positive(message = "Número da conta de origem deve ser positivo.")
    private int origem;

    @Positive(message = "Número da conta de destino deve ser positivo.")
    private int destino;

    @Positive(message = "O valor da transferência deve ser positivo.")
    private double valor;

    public int getOrigem() { return origem; }
    public void setOrigem(int origem) { this.origem = origem; }
    public int getDestino() { return destino; }
    public void setDestino(int destino) { this.destino = destino; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
}
