package pe.gob.mpfn.casilla.notifications.repository;

import pe.gob.mpfn.casilla.notifications.model.dto.SearchNotificationDto;
import pe.gob.mpfn.casilla.notifications.model.dto.SearchNotificationResponseDto;
import pe.gob.mpfn.casilla.notifications.model.dto.notification.ChangeFolderRequest;
import pe.gob.mpfn.casilla.notifications.model.dto.notification.NotificacionAdjunto;
import pe.gob.mpfn.casilla.notifications.model.dto.notification.NotificacionDetalle;

import java.util.List;
import java.util.Optional;

public interface CustomNotificationRepository {

    SearchNotificationResponseDto getNotifications(SearchNotificationDto searchNotificationDto);

    Optional<NotificacionDetalle> obtenerNotificacion(String personaId, String idNotificacion);

    List<NotificacionAdjunto> obtenerAdjuntos(String idNotificacion);

    boolean actualizarEstadoLeido(String notificacion);

    boolean archivarNotificaciones(String personaId, List<String> idNotificacion);

    boolean cambiarFlagNotificacion(ChangeFolderRequest folderRequest);
}
