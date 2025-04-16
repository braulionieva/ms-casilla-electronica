package pe.gob.mpfn.casilla.notifications.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ResentCodeDto {

    private String idCasilla;
    private String usuario;
    private int    idTipoVerificacion;
    private String ipUsuario;
    private String dispositivo;

}