package pe.gob.mpfn.casilla.notifications.model.dto.notification;

import java.sql.Timestamp;
import java.util.List;

public record NotificacionBandeja(
        String idNotificacion,
        String numeroCaso,
        String tipoCedula,
        String urgencia,
        String tipoSujeto,
        String estado,
        String numeroCedula,
        String estapaProcesal,
        String actoProcesal,
        String tramite,
        Timestamp fechaEnvio,
        String folder,
        String archivado,
        String nombreCompleto,
        List<NotificacionAdjunto> adjuntos,
        String destacado,
        String importante
) {

    public NotificacionBandeja(String idNotificacion, String numeroCaso, String tipoCedula, String urgencia, String tipoSujeto, String estado, String numeroCedula, String estapaProcesal, String actoProcesal, String tramite, Timestamp fechaEnvio, String folder, String archivado, String nombreCompleto, String destacado, String importante) {
        this(idNotificacion, numeroCaso, tipoCedula, urgencia, tipoSujeto, estado, numeroCedula, estapaProcesal, actoProcesal, tramite, fechaEnvio, folder, archivado, nombreCompleto, List.of(), destacado, importante);
    }
}
