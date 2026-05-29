package controller.dto;

public class TransferenciaRequest {
    private int origem;
    private int destino;
    private double valor;

    public int getOrigem() { return origem; }
    public void setOrigem(int origem) { this.origem = origem; }
    public int getDestino() { return destino; }
    public void setDestino(int destino) { this.destino = destino; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
}
