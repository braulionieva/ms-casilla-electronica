package pe.gob.mpfn.casilla.notifications.exception;

public class InvalidCodeException extends RuntimeException {
    public InvalidCodeException() {
    }

    public InvalidCodeException(String message) {
        super(message);
    }

    public InvalidCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
