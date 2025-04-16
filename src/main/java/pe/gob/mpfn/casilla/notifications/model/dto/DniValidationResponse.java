package pe.gob.mpfn.casilla.notifications.model.dto;

import lombok.Builder;

@Builder
public record DniValidationResponse(String digitoVerificacion,
                                    String nombrePadre,
                                    String nombreMadre,
                                    String apellidoMaternoMadre,
                                    String apellidoMaternoPadre,
                                    String apellidoPaternoMadre,
                                    String apellidoPaternoPadre) {
}
