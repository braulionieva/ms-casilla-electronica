package pe.gob.mpfn.casilla.notifications.event.email;

public record VerificationCode2FAEvent(String fullname, String code, String expiration, String recipient) {

}