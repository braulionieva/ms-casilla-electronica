package pe.gob.mpfn.casilla.notifications.model.dto;

import java.util.Date;

public record AffiliateResult(
        String correo
        , String coUsuario
        , String nombre
        , String apPat
        , String apMat,
        Date fechaRegistro) {
}
