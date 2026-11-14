package exception;

/**
 * Exceção lançada quando há saldo insuficiente para uma operação de saque ou transferência.
 */
public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(String message) {
        super(message);
    }
}
