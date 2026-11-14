package controller;

import controller.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import model.Conta;
import model.ContaCorrente;
import model.ContaPoupanca;
import model.Transacao;
import model.Tributavel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.Banco;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller REST para operações bancárias.
 * Erros são tratados centralmente pelo GlobalExceptionHandler.
 */
@Tag(name = "Contas", description = "Operações bancárias do MineBanco")
@RestController
@RequestMapping("/api/contas")
@CrossOrigin(origins = "${cors.allowed-origins:http://localhost:4200}")
public class ContaController {

    private final Banco banco;

    public ContaController(Banco banco) {
        this.banco = banco;
    }

    // -------------------------------------------------------------------------
    // Contas
    // -------------------------------------------------------------------------

    @Operation(summary = "Cria uma nova conta bancária")
    @PostMapping
    public ResponseEntity<ContaResponse> criarConta(@Valid @RequestBody CriarContaRequest req) {
        Conta conta;
        if ("CORRENTE".equalsIgnoreCase(req.getTipo())) {
            conta = new ContaCorrente(req.getNumero(), new model.Cliente(req.getNome(), req.getCpf()), req.getLimite());
        } else {
            conta = new ContaPoupanca(req.getNumero(), new model.Cliente(req.getNome(), req.getCpf()));
        }
        banco.criarConta(conta);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(conta));
    }

    @Operation(summary = "Lista todas as contas (paginado)")
    @GetMapping
    public ResponseEntity<List<ContaResponse>> listarContas(
            @Parameter(description = "Página (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size) {
        List<ContaResponse> responses = banco.listarContasPaginado(page, size).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Busca uma conta pelo número")
    @GetMapping("/{numero}")
    public ResponseEntity<ContaResponse> buscarConta(@PathVariable int numero) {
        return ResponseEntity.ok(toResponse(banco.buscarConta(numero)));
    }

    @Operation(summary = "Remove uma conta (somente se saldo for zero)")
    @DeleteMapping("/{numero}")
    public ResponseEntity<Void> removerConta(@PathVariable int numero) {
        banco.removerConta(numero);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------------------------
    // Operações financeiras
    // -------------------------------------------------------------------------

    @Operation(summary = "Deposita um valor na conta")
    @PostMapping("/{numero}/deposito")
    public ResponseEntity<ContaResponse> depositar(
            @PathVariable int numero,
            @Valid @RequestBody OperacaoRequest req) {
        banco.depositar(numero, req.getValor());
        return ResponseEntity.ok(toResponse(banco.buscarConta(numero)));
    }

    @Operation(summary = "Realiza saque na conta")
    @PostMapping("/{numero}/saque")
    public ResponseEntity<ContaResponse> sacar(
            @PathVariable int numero,
            @Valid @RequestBody OperacaoRequest req) {
        banco.sacar(numero, req.getValor());
        return ResponseEntity.ok(toResponse(banco.buscarConta(numero)));
    }

    @Operation(summary = "Transfere valor entre contas (operação atômica)")
    @PostMapping("/transferencia")
    public ResponseEntity<List<ContaResponse>> transferir(@Valid @RequestBody TransferenciaRequest req) {
        banco.transferir(req.getOrigem(), req.getDestino(), req.getValor());
        List<ContaResponse> resultado = List.of(
            toResponse(banco.buscarConta(req.getOrigem())),
            toResponse(banco.buscarConta(req.getDestino()))
        );
        return ResponseEntity.ok(resultado);
    }

    @Operation(summary = "Aplica rendimento em todas as contas poupança")
    @PostMapping("/rendimento")
    public ResponseEntity<Void> aplicarRendimento(@Valid @RequestBody RendimentoRequest req) {
        banco.aplicarRendimentoPoupancas(req.getTaxa());
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------------------------
    // Relatórios
    // -------------------------------------------------------------------------

    @Operation(summary = "Retorna o total estimado de impostos sobre contas correntes")
    @GetMapping("/impostos")
    public ResponseEntity<ImpostosResponse> relatorioImpostos() {
        double total = banco.calcularTotalImpostos();
        return ResponseEntity.ok(new ImpostosResponse(total));
    }

    @Operation(summary = "Retorna o extrato de transações de uma conta")
    @GetMapping("/{numero}/extrato")
    public ResponseEntity<List<TransacaoResponse>> extrato(@PathVariable int numero) {
        List<TransacaoResponse> responses = banco.buscarExtrato(numero).stream()
            .map(t -> new TransacaoResponse(
                t.getId(), t.getTipo(), t.getValor(),
                t.getContaRelacionada(), t.getDescricao(), t.getCriadoEm()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private ContaResponse toResponse(Conta conta) {
        String tipo = (conta instanceof ContaCorrente) ? "CORRENTE" : "POUPANCA";
        double limite = (conta instanceof ContaCorrente cc) ? cc.getLimite() : 0;
        double imposto = (conta instanceof Tributavel t) ? t.calcularImposto() : 0;
        return new ContaResponse(
            conta.getNumeroConta(), tipo, conta.getSaldo(), limite,
            conta.getCliente().getNome(), conta.getCliente().getCpf(), imposto
        );
    }

    // DTO interno simples para impostos
    public record ImpostosResponse(double totalImpostos) {}
}
