package pe.gob.mpfn.casilla.notifications.model.dto;

import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(@NotBlank String user, String token, String uid) {


    public ForgotPasswordRequest(String user, String token) {
        this(user, token, "");
    }
}
