package exception;

/**
 * Exceção lançada quando uma regra de negócio é violada.
 * Usada para situações como: excluir conta com saldo, limite inválido, etc.
 */
public class RegraDeNegocioException extends RuntimeException {
    public RegraDeNegocioException(String message) {
        super(message);
    }
}
