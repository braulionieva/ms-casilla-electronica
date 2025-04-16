package pe.gob.mpfn.casilla.notifications.exception;

public class NotificationNotFound extends RuntimeException {
    public NotificationNotFound() {
    }

    public NotificationNotFound(String message) {
        super(message);
    }

    public NotificationNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
