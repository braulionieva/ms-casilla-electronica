package pe.gob.mpfn.casilla.notifications.model.dto.notification;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class NotificacionDetalle {

    private String idNotificacion;
    private String nombreCompleto;
    private Timestamp fechaEnvio;
    private String cedula;
    private String tipoCedula;
    private String despacho;
    private String numeroCaso;
    private String entidad;
    private String tipoDomicilio;
    private String idTipoDomicilio;
    private List<NotificacionAdjunto> adjuntos;
    private String estado;
    private String archivado;

    public NotificacionDetalle(
            String idNotificacion, String nombreCompleto, Timestamp fechaEnvio,
            String cedula, String tipoCedula, String despacho, String numeroCaso,
            String entidad, String tipoDomicilio, String idTipoDomicilio, String estado, String archivado
    ) {
        this.idNotificacion = idNotificacion;
        this.nombreCompleto = nombreCompleto;
        this.fechaEnvio = fechaEnvio;
        this.cedula = cedula;
        this.tipoCedula = tipoCedula;
        this.despacho = despacho;
        this.numeroCaso = numeroCaso;
        this.entidad = entidad;
        this.tipoDomicilio = tipoDomicilio;
        this.idTipoDomicilio = idTipoDomicilio;
        this.estado = estado;
        this.archivado = archivado;
    }
}
