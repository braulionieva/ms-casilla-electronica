package pe.gob.mpfn.casilla.notifications.event.email;

public record ChangeEmailRequestEvent(String fullname, String code, String recipient) {
}
