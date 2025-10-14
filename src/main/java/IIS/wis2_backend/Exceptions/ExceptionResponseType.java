package IIS.wis2_backend.Exceptions;

/**
 * Structure modelling the JSON response body sent when an exception is thrown.
 */
public record ExceptionResponseType(String message) {
}