package pe.gob.mpfn.casilla.notifications.event.email;

public record ResetPasswordEvent(String fullname, String cuentaId, String recipient) {

}
