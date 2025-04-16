package pe.gob.mpfn.casilla.notifications.model.dto;

public record UpdateProfileRequest(
        boolean personaNatural,
        boolean abogado,
        String numeroCasilla,
        String idColegioAbogados,
        String numeroColegiatura // Can be null
) {
}
