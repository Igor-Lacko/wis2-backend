package IIS.wis2_backend.Exceptions.ExceptionTypes;

/**
 * Exception when arbitrary thing doesn't match another arbitrary thing when it should.
 */
public class DoesntMatchException extends RuntimeException {
    /**
     * Constructor.
     * 
     * @param message the exception message.
     */
    public DoesntMatchException(String message) {
        super(message);
    }
}