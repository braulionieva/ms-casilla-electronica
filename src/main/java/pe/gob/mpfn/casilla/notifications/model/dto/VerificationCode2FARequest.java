package pe.gob.mpfn.casilla.notifications.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class VerificationCode2FARequest {

    private String idCasilla;
    private int idTipoVerificacion;
    private String ipUsuario;
    private String dispositivo;
    private String usuario;
    private String code;
    private String soloBloqueo;

}