package pe.gob.mpfn.casilla.notifications.event.email;

public record VerificationCodeEvent(String fullname, String codigo,String email) {
}
