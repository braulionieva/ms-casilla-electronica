package pe.gob.mpfn.casilla.notifications.exception;

public class EmailServiceException extends RuntimeException {
    public EmailServiceException() {
    }

    public EmailServiceException(String message) {
        super(message);
    }

    public EmailServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
