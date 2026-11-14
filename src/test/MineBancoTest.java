package test;

import exception.SaldoInsuficienteException;
import model.Cliente;
import model.Conta;
import model.ContaCorrente;
import model.ContaPoupanca;
import org.junit.jupiter.api.*;
import util.Notificador;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários do MineBanco usando JUnit 5.
 * Execute com: mvn test
 */
@DisplayName("MineBanco - Testes Unitários")
class MineBancoTest {

    // =========================================================================
    // ContaCorrente
    // =========================================================================

    @Nested
    @DisplayName("ContaCorrente")
    class ContaCorrenteTests {

        private ContaCorrente cc;

        @BeforeEach
        void setup() {
            cc = new ContaCorrente(101, new Cliente("João", "12345678901"), 500.0);
        }

        @Test
        @DisplayName("Criação com dados corretos")
        void deveCriarContaCorrente() {
            assertEquals(101, cc.getNumeroConta());
            assertEquals(500.0, cc.getLimite(), 0.001);
            assertEquals(0.0, cc.getSaldo(), 0.001);
        }

        @Test
        @DisplayName("Limite negativo deve lançar exceção")
        void limiteNegativoDeveLancarExcecao() {
            assertThrows(IllegalArgumentException.class,
                () -> new ContaCorrente(102, new Cliente("Test", "11111111111"), -100.0));
        }

        @Test
        @DisplayName("Depósito válido aumenta saldo")
        void depositoValidoDeveAumentarSaldo() {
            cc.depositar(150.0);
            assertEquals(150.0, cc.getSaldo(), 0.001);
        }

        @Test
        @DisplayName("Depósito com valor zero lança exceção")
        void depositoZeroDeveLancarExcecao() {
            assertThrows(IllegalArgumentException.class, () -> cc.depositar(0));
        }

        @Test
        @DisplayName("Saque com saldo suficiente cobra taxa de R$ 2,00")
        void saqueComSaldoDeveDescontarTaxa() {
            cc.depositar(100.0);
            cc.sacar(40.0);
            assertEquals(58.0, cc.getSaldo(), 0.001); // 100 - 40 - 2 = 58
        }

        @Test
        @DisplayName("Saque usando limite (saldo zero)")
        void saqueComLimiteDevePermitirSaqueNegativo() {
            cc.sacar(100.0);
            assertEquals(-102.0, cc.getSaldo(), 0.001); // 0 - 100 - 2 = -102
        }

        @Test
        @DisplayName("Saque acima de saldo + limite lança SaldoInsuficienteException")
        void saqueAcimaDoLimiteDeveLancarExcecao() {
            assertThrows(SaldoInsuficienteException.class, () -> cc.sacar(500.0));
            assertEquals(0.0, cc.getSaldo(), 0.001); // saldo não alterado
        }

        @Test
        @DisplayName("Imposto = 1% do saldo positivo")
        void calcularImpostoDeveSer1PorCento() {
            cc.depositar(1000.0);
            assertEquals(10.0, cc.calcularImposto(), 0.001);
        }

        @Test
        @DisplayName("Imposto zero quando saldo é zero ou negativo")
        void calcularImpostoZeroQuandoSaldoNaoPositivo() {
            assertEquals(0.0, cc.calcularImposto(), 0.001);
        }
    }

    // =========================================================================
    // ContaPoupanca
    // =========================================================================

    @Nested
    @DisplayName("ContaPoupanca")
    class ContaPoupancaTests {

        private ContaPoupanca cp;

        @BeforeEach
        void setup() {
            cp = new ContaPoupanca(202, new Cliente("Maria", "98765432100"));
        }

        @Test
        @DisplayName("Saque sem saldo suficiente lança SaldoInsuficienteException")
        void saqueAcimaDoSaldoDeveLancarExcecao() {
            cp.depositar(50.0);
            assertThrows(SaldoInsuficienteException.class, () -> cp.sacar(60.0));
            assertEquals(50.0, cp.getSaldo(), 0.001); // saldo preservado
        }

        @Test
        @DisplayName("Rendimento de 1.5% sobre saldo")
        void rendimentoDeveAumentarSaldo() {
            cp.depositar(200.0);
            cp.aplicarRendimento(0.015);
            assertEquals(203.0, cp.getSaldo(), 0.001);
        }

        @Test
        @DisplayName("Taxa de rendimento zero lança exceção")
        void taxaZeroDeveLancarExcecao() {
            assertThrows(IllegalArgumentException.class, () -> cp.aplicarRendimento(0.0));
        }

        @Test
        @DisplayName("Taxa de rendimento negativa lança exceção")
        void taxaNegativaDeveLancarExcecao() {
            assertThrows(IllegalArgumentException.class, () -> cp.aplicarRendimento(-0.1));
        }
    }

    // =========================================================================
    // Conta — transferência
    // =========================================================================

    @Nested
    @DisplayName("Transferência entre contas")
    class TransferenciaTests {

        @Test
        @DisplayName("Transferência válida debita origem e credita destino")
        void transferenciaValidaDeveAtualizarSaldos() {
            ContaCorrente origem = new ContaCorrente(1, new Cliente("A", "11111111111"), 0);
            ContaPoupanca destino = new ContaPoupanca(2, new Cliente("B", "22222222222"));
            origem.depositar(500.0);
            origem.transferir(destino, 200.0);
            assertEquals(300.0, origem.getSaldo(), 0.001);
            assertEquals(200.0, destino.getSaldo(), 0.001);
        }

        @Test
        @DisplayName("Transferência acima do saldo lança SaldoInsuficienteException")
        void transferenciaAcimaDoSaldoDeveLancarExcecao() {
            ContaCorrente origem = new ContaCorrente(1, new Cliente("A", "11111111111"), 0);
            ContaPoupanca destino = new ContaPoupanca(2, new Cliente("B", "22222222222"));
            origem.depositar(100.0);
            assertThrows(SaldoInsuficienteException.class, () -> origem.transferir(destino, 200.0));
        }
    }

    // =========================================================================
    // service.Banco (com Notificador spy)
    // =========================================================================

    @Nested
    @DisplayName("Banco Service")
    class BancoServiceTests {

        /**
         * Testa o contrato do Notificador usando um spy simples.
         * O Banco.criarConta() deve chamar notificador.notificar() com o número da conta.
         */
        @Test
        @DisplayName("Notificador recebe mensagem ao criar conta")
        void notificadorDeveReceberMensagem() {
            class SpyNotificador implements Notificador {
                String ultimaMensagem;
                @Override public void notificar(String mensagem) { this.ultimaMensagem = mensagem; }
            }

            SpyNotificador spy = new SpyNotificador();

            // DAO anônimo que não faz nada (evita acesso ao banco real)
            util.ContaDAO daoMock = new util.ContaDAO(null) {
                @Override
                public void salvarConta(Conta conta) throws java.sql.SQLException { /* mock */ }
                @Override
                public Integer buscarIdClientePorCpf(String cpf) throws java.sql.SQLException { return 1; }
            };

            service.Banco banco = new service.Banco(daoMock);
            banco.setNotificador(spy);

            Conta cc = new ContaCorrente(101, new Cliente("Test", "12345678901"), 100.0);
            banco.criarConta(cc);

            assertNotNull(spy.ultimaMensagem, "Notificador deve ter sido chamado");
            assertTrue(spy.ultimaMensagem.contains("101"), "Mensagem deve conter o número da conta");
        }
    }

    // =========================================================================
    // Validador
    // =========================================================================

    @Nested
    @DisplayName("Validador")
    class ValidadorTests {

        @Test @DisplayName("CPF com 11 dígitos é válido")
        void cpfValido() { assertTrue(util.Validador.validarCPF("12345678901")); }

        @Test @DisplayName("CPF com menos de 11 dígitos é inválido")
        void cpfCurto() { assertFalse(util.Validador.validarCPF("12345")); }

        @Test @DisplayName("CPF nulo é inválido")
        void cpfNulo() { assertFalse(util.Validador.validarCPF(null)); }

        @Test @DisplayName("Valor positivo é válido")
        void valorPositivo() { assertTrue(util.Validador.validarValor(10.0)); }

        @Test @DisplayName("Valor zero é inválido")
        void valorZero() { assertFalse(util.Validador.validarValor(0.0)); }

        @Test @DisplayName("Valor negativo é inválido")
        void valorNegativo() { assertFalse(util.Validador.validarValor(-5.0)); }
    }
}
