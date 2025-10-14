package IIS.wis2_backend.Exceptions.ExceptionTypes;

/**
 * Exception thrown when trying to do arbitrary thing that has already 
 * been done, for example activating an already activated account.
 */
public class AlreadySetException extends RuntimeException {
    /**
     * Constructor for AlreadySetException.
     * 
     * @param message The exception message.
     */
    public AlreadySetException(String message) {
        super(message);
    }
}