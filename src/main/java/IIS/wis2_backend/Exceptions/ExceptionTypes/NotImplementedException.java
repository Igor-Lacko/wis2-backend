package IIS.wis2_backend.Exceptions.ExceptionTypes;

/**
 * Exception representing a not implemented feature.
 */
public class NotImplementedException extends RuntimeException {
    public NotImplementedException(String message) {
        super(message);
    }
}