package test;

import model.*;
import service.Banco;
import util.Validador;

public class TesteMineBanco {

    private static int testesPassados = 0;
    private static int testesFalhos = 0;

    public static void main(String[] args) {
        System.out.println("Iniciando os testes unitários do MineBanco...\n");

        runTest("testCriarContaCorrente", TesteMineBanco::testCriarContaCorrente);
        runTest("testDepositarCC", TesteMineBanco::testDepositarCC);
        runTest("testSacarCCComSaldo", TesteMineBanco::testSacarCCComSaldo);
        runTest("testSacarCCComLimite", TesteMineBanco::testSacarCCComLimite);
        runTest("testSacarCCSemSaldoLimite", TesteMineBanco::testSacarCCSemSaldoLimite);
        runTest("testCalcularImpostoCC", TesteMineBanco::testCalcularImpostoCC);
        runTest("testSacarCPSemSaldo", TesteMineBanco::testSacarCPSemSaldo);
        runTest("testAplicarRendimentoCP", TesteMineBanco::testAplicarRendimentoCP);
        runTest("testAdicionarEBuscarConta", TesteMineBanco::testAdicionarEBuscarConta);
        runTest("testCalcularTotalImpostosBanco", TesteMineBanco::testCalcularTotalImpostosBanco);
        runTest("testAplicarRendimentoPoupancasBanco", TesteMineBanco::testAplicarRendimentoPoupancasBanco);
        runTest("testValidarCPF", TesteMineBanco::testValidarCPF);
        runTest("testValidarValor", TesteMineBanco::testValidarValor);
        runTest("testNotificadorInterface", TesteMineBanco::testNotificadorInterface);

        System.out.println("\n=================================");
        System.out.println("Resumo dos Testes:");
        System.out.println("Passaram: " + testesPassados);
        System.out.println("Falharam: " + testesFalhos);
        System.out.println("=================================");
        
        if (testesFalhos > 0) {
            System.exit(1);
        }
    }

    private static void runTest(String nomeTeste, Runnable teste) {
        try {
            teste.run();
            System.out.println("[OK] - " + nomeTeste);
            testesPassados++;
        } catch (Throwable e) {
            System.err.println("[FALHA] - " + nomeTeste + ": " + e.getMessage());
            testesFalhos++;
        }
    }

    // Asserts simples
    private static void assertEquals(double esperado, double atual, double delta) {
        if (Math.abs(esperado - atual) > delta) {
            throw new AssertionError("Esperado: " + esperado + ", mas obtido: " + atual);
        }
    }

    private static void assertEquals(Object esperado, Object atual) {
        if (esperado == null && atual == null) return;
        if (esperado == null || !esperado.equals(atual)) {
            throw new AssertionError("Esperado: " + esperado + ", mas obtido: " + atual);
        }
    }

    private static void assertTrue(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }

    private static void assertFalse(boolean condicao, String mensagem) {
        if (condicao) {
            throw new AssertionError(mensagem);
        }
    }

    // --- TEST CASES ---
    
    private static void testCriarContaCorrente() {
        Cliente cliente = new Cliente("Teste CC", "12345678901");
        ContaCorrente cc = new ContaCorrente(101, cliente, 500.0);
        assertEquals(101, cc.getNumeroConta());
        assertEquals(500.0, cc.getLimite(), 0.001);
        assertEquals(cliente, cc.getCliente());
        assertEquals(0.0, cc.getSaldo(), 0.001);
    }

    private static void testDepositarCC() {
        Cliente cliente = new Cliente("Teste CC", "12345678901");
        ContaCorrente cc = new ContaCorrente(101, cliente, 500.0);
        cc.depositar(150.0);
        assertEquals(150.0, cc.getSaldo(), 0.001);
    }

    private static void testSacarCCComSaldo() {
        Cliente cliente = new Cliente("Teste CC", "12345678901");
        ContaCorrente cc = new ContaCorrente(101, cliente, 500.0);
        cc.depositar(100.0);
        cc.sacar(40.0); // Cobra taxa de 2.00. Saldo deve ser 58.00
        assertEquals(58.00, cc.getSaldo(), 0.001);
    }

    private static void testSacarCCComLimite() {
        Cliente cliente = new Cliente("Teste CC", "12345678901");
        ContaCorrente cc = new ContaCorrente(101, cliente, 500.0);
        cc.sacar(100.0); // Sem saldo inicial, usa limite. Cobra taxa de 2.00. Saldo deve ser -102.00
        assertEquals(-102.00, cc.getSaldo(), 0.001);
    }

    private static void testSacarCCSemSaldoLimite() {
        Cliente cliente = new Cliente("Teste CC", "12345678901");
        ContaCorrente cc = new ContaCorrente(101, cliente, 500.0);
        cc.sacar(500.0); // Tentativa de saque totalizando 502.00 (limite 500.00). Saldo deve permanecer 0.0
        assertEquals(0.0, cc.getSaldo(), 0.001);
    }

    private static void testCalcularImpostoCC() {
        Cliente cliente = new Cliente("Teste CC", "12345678901");
        ContaCorrente cc = new ContaCorrente(101, cliente, 500.0);
        cc.depositar(1000.0);
        assertEquals(10.0, cc.calcularImposto(), 0.001); // 1% de 1000 = 10
    }

    private static void testSacarCPSemSaldo() {
        Cliente cliente = new Cliente("Teste CP", "12345678901");
        ContaPoupanca cp = new ContaPoupanca(202, cliente);
        cp.depositar(50.0);
        cp.sacar(60.0); // Deve recusar e manter saldo 50.0
        assertEquals(50.0, cp.getSaldo(), 0.001);
    }

    private static void testAplicarRendimentoCP() {
        Cliente cliente = new Cliente("Teste CP", "12345678901");
        ContaPoupanca cp = new ContaPoupanca(202, cliente);
        cp.depositar(200.0);
        cp.aplicarRendimento(0.015); // Rendimento de 1.5% -> 3.00. Saldo deve ser 203.00
        assertEquals(203.00, cp.getSaldo(), 0.001);
    }

    private static void testAdicionarEBuscarConta() {
        Banco banco = new Banco();
        Cliente cliente = new Cliente("Teste CC", "12345678901");
        Conta cc = new ContaCorrente(101, cliente, 500.0);
        banco.criarConta(cc);
        assertEquals(cc, banco.buscarConta(101));
    }

    private static void testCalcularTotalImpostosBanco() {
        Banco banco = new Banco();
        Cliente cliente = new Cliente("Teste", "12345678901");
        ContaCorrente cc = new ContaCorrente(101, cliente, 500.0);
        ContaPoupanca cp = new ContaPoupanca(202, cliente);
        
        cc.depositar(100.0); // Imposto = 1.0
        cp.depositar(200.0); // Isento
        
        banco.criarConta(cc);
        banco.criarConta(cp);
        
        assertEquals(1.0, banco.calcularTotalImpostos(), 0.001);
    }

    private static void testAplicarRendimentoPoupancasBanco() {
        Banco banco = new Banco();
        Cliente cliente = new Cliente("Teste", "12345678901");
        ContaCorrente cc = new ContaCorrente(101, cliente, 500.0);
        ContaPoupanca cp = new ContaPoupanca(202, cliente);
        
        cc.depositar(100.0); 
        cp.depositar(100.0); 
        
        banco.criarConta(cc);
        banco.criarConta(cp);
        
        banco.aplicarRendimentoPoupancas(0.05); // Poupança rende 5% -> Novo Saldo = 105.0. CC continua 100.0
        
        assertEquals(100.0, cc.getSaldo(), 0.001);
        assertEquals(105.0, cp.getSaldo(), 0.001);
    }

    private static void testValidarCPF() {
        assertTrue(Validador.validarCPF("12345678901"), "CPF com 11 caracteres deveria ser válido");
        assertFalse(Validador.validarCPF("12345"), "CPF menor que 11 deveria ser inválido");
        assertFalse(Validador.validarCPF(null), "CPF nulo deveria ser inválido");
    }

    private static void testValidarValor() {
        assertTrue(Validador.validarValor(10.0), "Valor positivo deveria ser válido");
        assertFalse(Validador.validarValor(0.0), "Valor zero deveria ser inválido");
        assertFalse(Validador.validarValor(-5.0), "Valor negativo deveria ser inválido");
    }

    private static void testNotificadorInterface() {
        class SpyNotificador implements util.Notificador {
            String ultimaMensagem = null;
            @Override
            public void notificar(String mensagem) {
                this.ultimaMensagem = mensagem;
            }
        }
        
        Banco banco = new Banco();
        SpyNotificador spy = new SpyNotificador();
        banco.setNotificador(spy);
        
        Cliente cliente = new Cliente("Notif", "12345678901");
        Conta cc = new ContaCorrente(101, cliente, 100.0);
        
        banco.criarConta(cc);
        
        assertEquals("Conta criada com sucesso.", spy.ultimaMensagem);
    }
}
