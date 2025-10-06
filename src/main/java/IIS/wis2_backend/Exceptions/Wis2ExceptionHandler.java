package IIS.wis2_backend.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
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
     * @param e the UserAlreadyExistsException. Thrown (probably) during registration.
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
        return new ExceptionResponseType(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}