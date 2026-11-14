package model;

import java.time.LocalDateTime;

/**
 * Representa uma transação financeira registrada no histórico da conta.
 */
public class Transacao {
    private Long id;
    private String tipo;
    private int numeroConta;
    private double valor;
    private Integer contaRelacionada;
    private String descricao;
    private LocalDateTime criadoEm;

    public Transacao(Long id, String tipo, int numeroConta, double valor,
                     Integer contaRelacionada, String descricao, LocalDateTime criadoEm) {
        this.id = id;
        this.tipo = tipo;
        this.numeroConta = numeroConta;
        this.valor = valor;
        this.contaRelacionada = contaRelacionada;
        this.descricao = descricao;
        this.criadoEm = criadoEm;
    }

    public Long getId() { return id; }
    public String getTipo() { return tipo; }
    public int getNumeroConta() { return numeroConta; }
    public double getValor() { return valor; }
    public Integer getContaRelacionada() { return contaRelacionada; }
    public String getDescricao() { return descricao; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
}
