package pe.gob.mpfn.casilla.notifications.event.email;

public record ForgotPasswordEvent(String fullname, String email, String url) {
}
