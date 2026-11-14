package controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Resposta de erro padronizada para todos os endpoints da API.
 * Todos os erros retornam este mesmo formato JSON.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private int status;
    private String erro;
    private String mensagem;
    private LocalDateTime timestamp;
    private List<String> detalhes;

    public ErrorResponse(int status, String erro, String mensagem) {
        this.status = status;
        this.erro = erro;
        this.mensagem = mensagem;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String erro, String mensagem, List<String> detalhes) {
        this(status, erro, mensagem);
        this.detalhes = detalhes;
    }

    public int getStatus() { return status; }
    public String getErro() { return erro; }
    public String getMensagem() { return mensagem; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public List<String> getDetalhes() { return detalhes; }
}
