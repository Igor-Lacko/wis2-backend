package IIS.wis2_backend.Exceptions.ExceptionTypes;

/**
 * Exception thrown when an activation/password reset link has expired.
 */
public class LinkExpiredException extends RuntimeException {
    /**
     * Constructor for LinkExpiredException.
     * 
     * @param message The message to be included in the exception.
     * @param isActivationLink True if the link was for activation, false if for password reset.
     */
    public LinkExpiredException(String message, boolean isActivationLink) {
        super(isActivationLink ? ("Activation link expired: " + message) : ("Password reset link expired: " + message));
    }
}