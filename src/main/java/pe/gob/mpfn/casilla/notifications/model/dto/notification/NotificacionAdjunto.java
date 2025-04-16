package pe.gob.mpfn.casilla.notifications.model.dto.notification;

import java.util.Date;

public record NotificacionAdjunto(
        String idNotificacionAdjunto,
        String idDocumento,
        String coDocumento,
        String noDocumentoOrigen,
        Date fechaCreacion,
        Integer numeroOrden
) {

    public NotificacionAdjunto(int numeroOrden, Date fechaCreacion, String idNotificacionAdjunto, String idDocumento) {
        this(idNotificacionAdjunto, idDocumento, null, null, fechaCreacion, numeroOrden);
    }

}
