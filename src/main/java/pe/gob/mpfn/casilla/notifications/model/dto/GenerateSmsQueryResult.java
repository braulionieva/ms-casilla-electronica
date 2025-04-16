package pe.gob.mpfn.casilla.notifications.model.dto;

public record GenerateSmsQueryResult(String smsCode, String statusCode, String statusMessage, String nombreCompleto) {
}
