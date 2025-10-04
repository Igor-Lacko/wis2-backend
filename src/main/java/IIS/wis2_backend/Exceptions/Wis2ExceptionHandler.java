package IIS.wis2_backend.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import IIS.wis2_backend.Exceptions.ExceptionTypes.UserAlreadyExistsException;

/**
 * Global exception handler class for mapping exceptions to HTTP responses.
 */
@ControllerAdvice
public class Wis2ExceptionHandler {
    @ExceptionHandler(value = UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public @ResponseBody ExceptionResponseType handleUserAlreadyExists(UserAlreadyExistsException e) {
        return new ExceptionResponseType(e.getMessage(), HttpStatus.CONFLICT);
    }
}