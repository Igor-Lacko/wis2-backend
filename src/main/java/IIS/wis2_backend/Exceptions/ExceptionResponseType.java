package IIS.wis2_backend.Exceptions;

import org.springframework.http.HttpStatus;

/**
 * Structure modelling the JSON response body sent when an exception is thrown.
 */
public record ExceptionResponseType(String message, HttpStatus status) {
}