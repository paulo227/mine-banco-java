package app;

import model.Cliente;
import model.Conta;
import model.ContaCorrente;
import model.ContaPoupanca;
import service.Banco;

import java.util.Scanner;

public class Menu {

    Scanner scanner = new Scanner(System.in);
    Banco banco = new Banco();

    public void iniciar() {

        int opcao;

        do {

            System.out.println("\n===== MINI BANCO =====");
            System.out.println("1 - Criar conta");
            System.out.println("2 - Depositar");
            System.out.println("3 - Sacar");
            System.out.println("4 - Transferir");
            System.out.println("5 - Listar contas");
            System.out.println("6 - Aplicar rendimento na poupança");
            System.out.println("7 - Relatório de impostos");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");

            opcao = scanner.nextInt();

            switch (opcao) {

                case 1:
                    criarConta();
                    break;

                case 2:
                    depositar();
                    break;

                case 3:
                    sacar();
                    break;

                case 4:
                    transferir();
                    break;

                case 5:
                    banco.listarContas();
                    break;

                case 6:
                    aplicarRendimento();
                    break;

                case 7:
                    mostrarRelatorioImpostos();
                    break;

                case 0:
                    System.out.println("Sistema encerrado.");
                    break;

                default:
                    System.out.println("Opção inválida.");
            }

            if (opcao != 0) {
                aguardar();
            }

        } while (opcao != 0);

    }

    private void criarConta() {

        scanner.nextLine();

        System.out.print("Nome do cliente: ");
        String nome = scanner.nextLine();

        System.out.print("CPF: ");
        String cpf = scanner.nextLine();

        System.out.print("Número da conta: ");
        int numero = scanner.nextInt();

        System.out.println("Escolha o tipo de conta:");
        System.out.println("1 - Conta Corrente");
        System.out.println("2 - Conta Poupança");
        System.out.print("Opção: ");
        int tipo = scanner.nextInt();

        Cliente cliente = new Cliente(nome, cpf);
        Conta conta = null;

        if (tipo == 1) {
            System.out.print("Limite do cheque especial: ");
            double limite = scanner.nextDouble();
            conta = new ContaCorrente(numero, cliente, limite);
        } else if (tipo == 2) {
            conta = new ContaPoupanca(numero, cliente);
        } else {
            System.out.println("Tipo inválido. Conta não criada.");
            return;
        }

        banco.criarConta(conta);

    }

    private void depositar() {

        System.out.print("Número da conta: ");
        int numero = scanner.nextInt();

        System.out.print("Valor do depósito: ");
        double valor = scanner.nextDouble();

        banco.depositar(numero, valor);

    }

    private void sacar() {

        System.out.print("Número da conta: ");
        int numero = scanner.nextInt();

        System.out.print("Valor do saque: ");
        double valor = scanner.nextDouble();

        banco.sacar(numero, valor);

    }

    private void transferir() {

        System.out.print("Conta origem: ");
        int origem = scanner.nextInt();

        System.out.print("Conta destino: ");
        int destino = scanner.nextInt();

        System.out.print("Valor: ");
        double valor = scanner.nextDouble();

        banco.transferir(origem, destino, valor);

    }

    private void aplicarRendimento() {
        System.out.print("Digite a taxa de rendimento (ex: 0,01 para 1%): ");
        double taxa = scanner.nextDouble();
        banco.aplicarRendimentoPoupancas(taxa);
    }

    private void mostrarRelatorioImpostos() {
        double total = banco.calcularTotalImpostos();
        System.out.println("===== RELATÓRIO DE IMPOSTOS =====");
        System.out.println("Total de tributos arrecadados: R$ " + total);
    }

    private void aguardar() {
        try {
            Thread.sleep(300); // Aguarda 1.5 segundos para o usuário ler o resultado da ação
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
