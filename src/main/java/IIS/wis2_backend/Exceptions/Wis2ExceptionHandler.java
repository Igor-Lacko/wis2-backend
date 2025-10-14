package IIS.wis2_backend.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import IIS.wis2_backend.Exceptions.ExceptionTypes.UserAlreadyExistsException;

/**
 * Global exception handler class for mapping exceptions to HTTP responses.
 */
@ControllerAdvice
public class Wis2ExceptionHandler {
    /**
     * Handler for UserAlreadyExistsException.
     * 
     * @param e the UserAlreadyExistsException. Thrown (probably) during
     *          registration.
     */
    @ExceptionHandler(value = UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public @ResponseBody ExceptionResponseType handleUserAlreadyExists(UserAlreadyExistsException e) {
        return new ExceptionResponseType(e.getMessage(), HttpStatus.CONFLICT);
    }

    /**
     * Handler for AuthenticationException.
     * 
     * @param e the AuthenticationException. Thrown during login.
     */
    @ExceptionHandler(value = AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public @ResponseBody ExceptionResponseType handleAuthenticationException(AuthenticationException e) {
        return new ExceptionResponseType("Login failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handler for MethodArgumentNotValidException.
     * 
     * @param e the MethodArgumentNotValidException. Thrown when a method argument
     *          annotated with @Valid fails validation.
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ExceptionResponseType handleInvalidMethodArgument(MethodArgumentNotValidException e) {
        return new ExceptionResponseType(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handler for MailException.
     * 
     * @param e the MailException. Thrown when sending an email fails.
     */
    @ExceptionHandler(value = MailException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody ExceptionResponseType handleMailException(MailException e) {
        return new ExceptionResponseType("Mail sending failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handler for DisabledException. Occurs when a unactivated user tries to log in.
     * 
     * @param e the DisabledException.
     */
    @ExceptionHandler(value = DisabledException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public @ResponseBody ExceptionResponseType handleDisabledException(DisabledException e) {
        return new ExceptionResponseType("User is not activated: " + e.getMessage(), HttpStatus.FORBIDDEN);
    }
}