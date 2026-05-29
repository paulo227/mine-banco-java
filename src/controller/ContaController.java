package controller;

import controller.dto.*;
import model.Cliente;
import model.Conta;
import model.ContaCorrente;
import model.ContaPoupanca;
import model.Tributavel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.Banco;
import util.ContaDAO;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/contas")
@CrossOrigin(origins = "*")
public class ContaController {

    private final Banco banco = new Banco();
    private final ContaDAO contaDAO = new ContaDAO();

    @PostMapping
    public ResponseEntity<?> criarConta(@RequestBody CriarContaRequest req) {
        try {
            Cliente cliente = new Cliente(req.getNome(), req.getCpf());
            Conta conta;
            if (req.getTipo() == 1) {
                conta = new ContaCorrente(req.getNumero(), cliente, req.getLimite());
            } else if (req.getTipo() == 2) {
                conta = new ContaPoupanca(req.getNumero(), cliente);
            } else {
                return ResponseEntity.badRequest().body("Tipo inválido. Use 1 (Corrente) ou 2 (Poupança).");
            }
            banco.criarConta(conta);
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(conta));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar conta: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<ContaResponse>> listarContas() {
        try {
            List<Conta> contas = contaDAO.listarContas();
            List<ContaResponse> responses = new ArrayList<>();
            for (Conta c : contas) {
                responses.add(toResponse(c));
            }
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{numero}")
    public ResponseEntity<?> buscarConta(@PathVariable int numero) {
        try {
            Conta conta = contaDAO.buscarConta(numero);
            if (conta == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Conta não encontrada.");
            }
            return ResponseEntity.ok(toResponse(conta));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar conta: " + e.getMessage());
        }
    }

    @PostMapping("/{numero}/deposito")
    public ResponseEntity<?> depositar(@PathVariable int numero, @RequestBody OperacaoRequest req) {
        try {
            banco.depositar(numero, req.getValor());
            Conta conta = contaDAO.buscarConta(numero);
            if (conta == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Conta não encontrada.");
            }
            return ResponseEntity.ok(toResponse(conta));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao depositar: " + e.getMessage());
        }
    }

    @PostMapping("/{numero}/saque")
    public ResponseEntity<?> sacar(@PathVariable int numero, @RequestBody OperacaoRequest req) {
        try {
            banco.sacar(numero, req.getValor());
            Conta conta = contaDAO.buscarConta(numero);
            if (conta == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Conta não encontrada.");
            }
            return ResponseEntity.ok(toResponse(conta));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao sacar: " + e.getMessage());
        }
    }

    @PostMapping("/transferencia")
    public ResponseEntity<?> transferir(@RequestBody TransferenciaRequest req) {
        try {
            banco.transferir(req.getOrigem(), req.getDestino(), req.getValor());
            Conta origem = contaDAO.buscarConta(req.getOrigem());
            Conta destino = contaDAO.buscarConta(req.getDestino());
            if (origem == null || destino == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Conta de origem ou destino não encontrada.");
            }
            List<ContaResponse> resultado = new ArrayList<>();
            resultado.add(toResponse(origem));
            resultado.add(toResponse(destino));
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao transferir: " + e.getMessage());
        }
    }

    @PostMapping("/rendimento")
    public ResponseEntity<?> aplicarRendimento(@RequestBody RendimentoRequest req) {
        try {
            banco.aplicarRendimentoPoupancas(req.getTaxa());
            return ResponseEntity.ok("Rendimento aplicado com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao aplicar rendimento: " + e.getMessage());
        }
    }

    @GetMapping("/impostos")
    public ResponseEntity<?> relatorioImpostos() {
        try {
            double total = banco.calcularTotalImpostos();
            return ResponseEntity.ok("{\"totalImpostos\": " + total + "}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao calcular impostos: " + e.getMessage());
        }
    }

    @DeleteMapping("/{numero}")
    public ResponseEntity<?> removerConta(@PathVariable int numero) {
        try {
            banco.removerConta(numero);
            return ResponseEntity.ok("Conta removida com sucesso.");
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("não encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
            }
            if (msg != null && msg.contains("saldo diferente de zero")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao remover conta: " + msg);
        }
    }

    private ContaResponse toResponse(Conta conta) {
        String tipo = (conta instanceof ContaCorrente) ? "CORRENTE" : "POUPANCA";
        double limite = (conta instanceof ContaCorrente) ? ((ContaCorrente) conta).getLimite() : 0;
        double imposto = (conta instanceof Tributavel) ? ((Tributavel) conta).calcularImposto() : 0;
        return new ContaResponse(
                conta.getNumeroConta(),
                tipo,
                conta.getSaldo(),
                limite,
                conta.getCliente().getNome(),
                conta.getCliente().getCpf(),
                imposto
        );
    }
}
