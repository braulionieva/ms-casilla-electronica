package pe.gob.mpfn.casilla.notifications.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.mpfn.casilla.notifications.exception.HttpStatusResponseException;
import pe.gob.mpfn.casilla.notifications.exception.NotificationNotFound;
import pe.gob.mpfn.casilla.notifications.model.dto.SearchNotificationDto;
import pe.gob.mpfn.casilla.notifications.model.dto.SearchNotificationResponseDto;
import pe.gob.mpfn.casilla.notifications.model.dto.ValidateCodeResponse;
import pe.gob.mpfn.casilla.notifications.model.dto.notification.ChangeFolderRequest;
import pe.gob.mpfn.casilla.notifications.model.dto.notification.NotificacionDetalle;
import pe.gob.mpfn.casilla.notifications.model.entity.Notification;
import pe.gob.mpfn.casilla.notifications.repository.NotificationRepository;
import pe.gob.mpfn.casilla.notifications.util.enums.Folder;
import pe.gob.mpfn.casilla.notifications.util.enums.NotificationStatus;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository notificationRepository;

    public SearchNotificationResponseDto getNotificationsByDni(SearchNotificationDto searchNotificationDto) {
        return notificationRepository.getNotifications(searchNotificationDto);
    }

    @Transactional
    public NotificacionDetalle getByIdAndUser(String notId, String idCasilla) {
        var notif = notificationRepository.obtenerNotificacion(idCasilla, notId)
                .orElseThrow(() -> new NotificationNotFound("La notificación solicitada no existe"));
        var attachmentList = notificationRepository.obtenerAdjuntos(notif.getIdNotificacion());
        notif.setAdjuntos(attachmentList);

        if (NotificationStatus.RECEIVED.getValue().equals(notif.getEstado())) {
            notificationRepository.actualizarEstadoLeido(notif.getIdNotificacion());
        }

        return notif;
    }

    public List<Notification> getByTag(String id, String dni) {
        return notificationRepository.findByAffiliateIdAndTag(dni, id);
    }

    public List<Notification> getByFolder(String id, String dni) {
        return notificationRepository.findByAffiliateIdAndFolder(dni, id);
    }

    @Transactional
    public ValidateCodeResponse changeFolder(ChangeFolderRequest folderRequest) {

        var folders = Arrays.asList(Folder.DESTACADO.getKey(), Folder.IMPORTANTE.getKey());

        if (!folders.contains(folderRequest.getFolderValue())) {
            log.warn("Folder inválido - id: {}", folderRequest);
            throw new HttpStatusResponseException(HttpStatus.BAD_REQUEST, "Folder Inválido");
        }

        if (notificationRepository.cambiarFlagNotificacion(folderRequest))
            return new ValidateCodeResponse(true, "OK");
        else
            throw new HttpStatusResponseException(HttpStatus.INTERNAL_SERVER_ERROR, "Ha ocurrido un error, inténtelo más tarde");
    }

    public ValidateCodeResponse archivarNotificacion(ChangeFolderRequest folderRequest) {

        var response = notificationRepository.archivarNotificaciones(folderRequest.getCasillaId(), folderRequest.getNotifId());

        if (!response) {
            throw new HttpStatusResponseException(HttpStatus.INTERNAL_SERVER_ERROR, "Ha ocurrido un error, inténtelo más tarde");
        }

        return new ValidateCodeResponse(true, "OK");
    }
}
