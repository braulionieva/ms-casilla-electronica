package pe.gob.mpfn.casilla.notifications.exception;

public class InactiveAccountException extends RuntimeException {

    public InactiveAccountException() {
    }

    public InactiveAccountException(String message) {
        super(message);
    }

    public InactiveAccountException(String message, Throwable cause) {
        super(message, cause);
    }
}
