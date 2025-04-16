package pe.gob.mpfn.casilla.notifications.model.dto.rest;

public record BandejaFiscalResponse(String message, int code, String id, PersonData data) {
}
record PersonData(
        int idNTipoPersona,
        int idNTipoDocIdent,
        String nuVDocumento,
        String noVCiudadano,
        String apVPaterno,
        String apVMaterno,
        String deVRazonSocial
) {}
