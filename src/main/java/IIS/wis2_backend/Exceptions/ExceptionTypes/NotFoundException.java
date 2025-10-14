package IIS.wis2_backend.Exceptions.ExceptionTypes;

/**
 * Exception thrown when a user is not found in the database.
 */
public class NotFoundException extends RuntimeException {
    /**
     * Constructor for UserNotFoundException.
     * 
     * @param message the exception message.
     */
    public NotFoundException(String message) {
        super("User not found: " + message);
    }
}