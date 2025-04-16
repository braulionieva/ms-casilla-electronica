package pe.gob.mpfn.casilla.notifications.model.dto.rest;

import lombok.Data;

@Data
public class ReniecDniRequest {
    private String ip;
    private String numeroDocumento;
    private String usuarioConsulta;
    private String httpHost;
}
