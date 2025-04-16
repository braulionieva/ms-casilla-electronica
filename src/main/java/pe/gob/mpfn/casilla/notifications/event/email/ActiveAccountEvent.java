package pe.gob.mpfn.casilla.notifications.event.email;

public record ActiveAccountEvent(String fullname, String cuentaId, String recipient) {

}
