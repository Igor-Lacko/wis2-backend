package IIS.wis2_backend.Exceptions.ExceptionTypes;

/**
 * Exception representing an internal server error.
 */
public class InternalException extends RuntimeException {
    /**
     * Constructs a new InternalException with the specified detail message.
     * @param message the detail message
     */
    public InternalException(String message) {
        super("Internal Server Error: " + message);
    }
}