package pe.gob.mpfn.casilla.notifications.model.dto.rest;

import java.util.List;

public record ObtieneDocumentoResponse(int code, String message, String idDocumento, List<DocumentData> data) {
}

