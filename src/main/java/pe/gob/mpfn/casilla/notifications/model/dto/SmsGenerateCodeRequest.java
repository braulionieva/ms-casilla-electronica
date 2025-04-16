package pe.gob.mpfn.casilla.notifications.model.dto;

import jakarta.validation.constraints.NotBlank;

public record SmsGenerateCodeRequest(
        String code,
        @NotBlank String email,
        String ip,
        String nombres,
        @NotBlank String numeroDocumento,
        @NotBlank String celular
) {
    public SmsGenerateCodeRequest(String code, String email1) {
        this(code, email1, null, null, null, null);
    }


    public SmsGenerateCodeRequest(String email, String celular, String numeroDocumento) {
        this("", email, "", null, numeroDocumento, celular);
    }
}
