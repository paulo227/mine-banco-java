package controller.dto;

import java.time.LocalDateTime;

/**
 * DTO de resposta para o extrato de transações de uma conta.
 */
public class TransacaoResponse {
    private Long id;
    private String tipo;
    private double valor;
    private Integer contaRelacionada;
    private String descricao;
    private LocalDateTime criadoEm;

    public TransacaoResponse(Long id, String tipo, double valor,
                             Integer contaRelacionada, String descricao, LocalDateTime criadoEm) {
        this.id = id;
        this.tipo = tipo;
        this.valor = valor;
        this.contaRelacionada = contaRelacionada;
        this.descricao = descricao;
        this.criadoEm = criadoEm;
    }

    public Long getId() { return id; }
    public String getTipo() { return tipo; }
    public double getValor() { return valor; }
    public Integer getContaRelacionada() { return contaRelacionada; }
    public String getDescricao() { return descricao; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
}
