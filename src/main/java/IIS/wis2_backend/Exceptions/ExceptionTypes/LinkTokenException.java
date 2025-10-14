package IIS.wis2_backend.Exceptions.ExceptionTypes;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when an account activation fails, either due to an expired or invalid token.
 */
public class LinkTokenException extends RuntimeException {
    /**
     * Status code to be returned.
     */
    public final HttpStatus status;

    /**
     * Constructor for ActivationException.
     * 
     * @param message The message to be included in the exception.
     * @param isActivation True if the exception is for account activation, false if for password reset.
     * @param status The HTTP status to be returned, since this exception can be thrown in different contexts.
     */
    public LinkTokenException(String message, boolean isActivation, HttpStatus status) {
        super(isActivation ? "Account activation failed: " + message
                : "Password reset failed: " + message);
        this.status = status;
    }
}