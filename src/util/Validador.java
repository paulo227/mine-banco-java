package util;
public class Validador {

    // Validar valor positivo
    public static boolean validarValor(double valor) {

        return valor > 0;

    }

    // Validar CPF simples
    public static boolean validarCPF(String cpf) {

        return cpf != null && cpf.length() == 11;

    }

    // Validar número da conta
    public static boolean validarConta(int numeroConta) {

        return numeroConta > 0;

    }
}