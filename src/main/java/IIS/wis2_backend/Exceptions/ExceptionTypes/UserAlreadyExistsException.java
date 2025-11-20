package IIS.wis2_backend.Exceptions.ExceptionTypes;

/**
 * Exception thrown when a user tries to register with an email that already exists.
 * Also has a username variant since it might come in handy later. (Maybe for the admin?)
 */
public class UserAlreadyExistsException extends RuntimeException {
    /**
     * Constructor for UserAlreadyExistsException with a custom message.
     * 
     * @param message Custom error message.
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }

    /**
     * Constructor for UserAlreadyExistsException.
     * 
     * @param exceptionCause The email/username that caused the exception.
     * @param isEmail True if the cause is an email, false if it's a username.
     */
    public UserAlreadyExistsException(String exceptionCause, boolean isEmail) {
        super(isEmail ? "A user with the email " + exceptionCause + " already exists."
                : "A user with the username " + exceptionCause + " already exists.");
    }
}