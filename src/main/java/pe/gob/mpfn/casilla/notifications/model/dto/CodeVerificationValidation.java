package pe.gob.mpfn.casilla.notifications.model.dto;

import lombok.Builder;

@Builder
public record CodeVerificationValidation(String idCasilla,
                                         Long idTipoVerificacion,
                                         String ipUsuario,
                                         String dispositivo,
                                         String usuarioCreacion) {
}
