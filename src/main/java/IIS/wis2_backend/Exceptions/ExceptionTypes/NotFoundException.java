package IIS.wis2_backend.Exceptions.ExceptionTypes;

/**
 * Exception thrown when a arbitrary thing (mainly user) is not found.
 */
public class NotFoundException extends RuntimeException {
    /**
     * Constructor for NotFoundException.
     * 
     * @param message the exception message.
     */
    public NotFoundException(String message) {
        super(message);
    }
}