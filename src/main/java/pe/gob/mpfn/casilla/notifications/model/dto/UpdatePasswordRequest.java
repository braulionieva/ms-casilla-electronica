package pe.gob.mpfn.casilla.notifications.model.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePasswordRequest(@NotBlank String userId,
                                    @NotBlank String pw,
                                    String oldPw,
                                    String token,
                                    String codigoVerificacion,
                                    Long idTipoVerificacion) {

    public UpdatePasswordRequest(String userId, String pw, String code) {
        this(userId, pw, null, code,null,null);
    }

    public UpdatePasswordRequest(String userId, String pw, String oldPw, String token) {
        this(userId, pw, oldPw, token,null,null);
    }
}
