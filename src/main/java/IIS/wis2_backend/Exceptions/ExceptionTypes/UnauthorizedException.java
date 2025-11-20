package IIS.wis2_backend.Exceptions.ExceptionTypes;

/**
 * Exception thrown when trying to do arbitrary thing that the user is not authorized to do.
 */
public class UnauthorizedException extends RuntimeException {
    /**
     * Constructor for UnauthorizedException.
     * 
     * @param message The exception message.
     */
    public UnauthorizedException(String message) {
        super(message);
    }
}
