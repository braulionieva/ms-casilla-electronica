package pe.gob.mpfn.casilla.notifications.exception;

public class AffiliateNotFoundException extends RuntimeException {
    public AffiliateNotFoundException() {
    }

    public AffiliateNotFoundException(String message) {
        super(message);
    }

    public AffiliateNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
