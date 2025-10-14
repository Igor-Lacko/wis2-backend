package IIS.wis2_backend.Exceptions.ExceptionTypes;

/**
 * Exception thrown when an account activation fails, either due to an expired or invalid token.
 */
public class ActivationException extends RuntimeException {
    /**
     * Constructor for ActivationException.
     * 
     * @param message The message to be included in the exception.
     */
    public ActivationException(String message) {
        super("Account activation failed: " + message);
    }
}