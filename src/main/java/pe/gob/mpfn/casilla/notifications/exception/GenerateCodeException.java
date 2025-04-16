package pe.gob.mpfn.casilla.notifications.exception;

public class GenerateCodeException extends RuntimeException {
    public GenerateCodeException() {
    }

    public GenerateCodeException(String message) {
        super(message);
    }

    public GenerateCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
