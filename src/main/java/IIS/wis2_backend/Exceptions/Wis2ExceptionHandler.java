package IIS.wis2_backend.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import IIS.wis2_backend.Exceptions.ExceptionTypes.AlreadySetException;
import IIS.wis2_backend.Exceptions.ExceptionTypes.DoesntMatchException;
import IIS.wis2_backend.Exceptions.ExceptionTypes.InternalException;
import IIS.wis2_backend.Exceptions.ExceptionTypes.LinkExpiredException;
import IIS.wis2_backend.Exceptions.ExceptionTypes.UserAlreadyExistsException;
import IIS.wis2_backend.Exceptions.ExceptionTypes.NotFoundException;

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
        return new ExceptionResponseType(e.getMessage());
    }

    /**
     * Handler for IllegalArgumentException.
     * 
     * @param e the IllegalArgumentException. Thrown when a method receives an
     *          illegal or inappropriate argument.
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ExceptionResponseType handleIllegalArgumentException(IllegalArgumentException e) {
        return new ExceptionResponseType(e.getMessage());
    }

    /**
     * Handler for AuthenticationException.
     * 
     * @param e the AuthenticationException. Thrown during login.
     */
    @ExceptionHandler(value = AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public @ResponseBody ExceptionResponseType handleAuthenticationException(AuthenticationException e) {
        return new ExceptionResponseType("Login failed: " + e.getMessage());
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
        return new ExceptionResponseType(e.getMessage());
    }

    /**
     * Handler for MailException.
     * 
     * @param e the MailException. Thrown when sending an email fails.
     */
    @ExceptionHandler(value = MailException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody ExceptionResponseType handleMailException(MailException e) {
        return new ExceptionResponseType("Mail sending failed: " + e.getMessage());
    }

    /**
     * Handler for DisabledException. Occurs when a unactivated user tries to log in.
     * 
     * @param e the DisabledException.
     */
    @ExceptionHandler(value = DisabledException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public @ResponseBody ExceptionResponseType handleDisabledException(DisabledException e) {
        return new ExceptionResponseType("User is not activated: " + e.getMessage());
    }

    /**
     * Handler for LinkExpiredException, which occurs when an account activation link or password reset link has expired.
     * 
     * @param e the LinkExpiredException.
     */
    @ExceptionHandler(value = LinkExpiredException.class)
    @ResponseStatus(HttpStatus.GONE)
    public @ResponseBody ExceptionResponseType handleLinkExpiredException(LinkExpiredException e) {
        return new ExceptionResponseType(e.getMessage());
    }

    /**
     * Handler for UserNotFoundException.
     * 
     * @param e the UserNotFoundException.
     */
    @ExceptionHandler(value = NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody ExceptionResponseType handleUserNotFoundException(NotFoundException e) {
        return new ExceptionResponseType(e.getMessage());
    }

    /**
     * Handler for AlreadySetException.
     * 
     * @param e the AlreadySetException.
     */
    @ExceptionHandler(value = AlreadySetException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public @ResponseBody ExceptionResponseType handleAlreadySetException(AlreadySetException e) {
        return new ExceptionResponseType(e.getMessage());
    }

    /**
     * Handler for DoesntMatchException
     * 
     * @param e the DoesntMatchException
     */
    @ExceptionHandler(value = DoesntMatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ExceptionResponseType handleDoesntMatchException(DoesntMatchException e) {
        return new ExceptionResponseType(e.getMessage());
    }

    /**
     * Handler for InternalException.
     * 
     * @param e the InternalException.
     */
    @ExceptionHandler(value = InternalException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody ExceptionResponseType handleInternalException(InternalException e) {
        return new ExceptionResponseType(e.getMessage());
    }
}