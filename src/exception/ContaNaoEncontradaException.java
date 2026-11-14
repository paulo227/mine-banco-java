package exception;

/**
 * Exceção lançada quando uma conta não é encontrada no sistema.
 * Permite ao controller retornar HTTP 404 de forma explícita.
 */
public class ContaNaoEncontradaException extends RuntimeException {
    public ContaNaoEncontradaException(int numeroConta) {
        super("Conta " + numeroConta + " não encontrada.");
    }
    public ContaNaoEncontradaException(String message) {
        super(message);
    }
}
