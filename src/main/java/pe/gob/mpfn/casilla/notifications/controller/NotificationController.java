package pe.gob.mpfn.casilla.notifications.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.gob.mpfn.casilla.notifications.model.dto.SearchNotificationDto;
import pe.gob.mpfn.casilla.notifications.model.dto.SearchNotificationResponseDto;
import pe.gob.mpfn.casilla.notifications.model.dto.ValidateCodeResponse;
import pe.gob.mpfn.casilla.notifications.model.dto.notification.ChangeFolderRequest;
import pe.gob.mpfn.casilla.notifications.model.dto.notification.NotificacionDetalle;
import pe.gob.mpfn.casilla.notifications.model.user.UserDetail;
import pe.gob.mpfn.casilla.notifications.security.DocumentosGeneralesService;
import pe.gob.mpfn.casilla.notifications.service.NotificationService;

@RestController
@RequestMapping("account/notification")
public class NotificationController {

    private final NotificationService notificationService;
    private final DocumentosGeneralesService documentosGeneralesService;

    public NotificationController(NotificationService notificationService, DocumentosGeneralesService documentosGeneralesService) {
        this.notificationService = notificationService;
        this.documentosGeneralesService = documentosGeneralesService;
    }

    @GetMapping()
    public SearchNotificationResponseDto getNotifications(
            Authentication authentication,
            SearchNotificationDto searchNotificationDto
    ) {
        var user = ((UserDetail) authentication.getPrincipal());
        var casilla = user.getIdCasilla();
        var idPersona = user.getIdPersona();
        searchNotificationDto.setIdCasilla(casilla);
        searchNotificationDto.setIdPersona(idPersona);
        searchNotificationDto.setSessionType(user.getSession());
        return notificationService.getNotificationsByDni(searchNotificationDto);
    }

    @GetMapping("/{id}")
    public NotificacionDetalle getById(
            @PathVariable("id") String id,
            Authentication authentication) {
        var casilla = ((UserDetail) authentication.getPrincipal()).getIdCasilla();
        return notificationService.getByIdAndUser(id, casilla);
    }

    @PostMapping("/folder")
    public ValidateCodeResponse updateFolder(
            Authentication authentication,
            @RequestBody ChangeFolderRequest folderRequest
    ) {

        var idCasilla = ((UserDetail) authentication.getPrincipal()).getIdCasilla();
        folderRequest.setCasillaId(idCasilla);
        return notificationService.changeFolder(folderRequest);
    }

    @PostMapping("/folder/archive")
    public ValidateCodeResponse archivarNotificacion(
            Authentication authentication,
            @RequestBody ChangeFolderRequest folderRequest
    ) {
        var idCasilla = ((UserDetail) authentication.getPrincipal()).getIdCasilla();
        folderRequest.setCasillaId(idCasilla);
        return notificationService.archivarNotificacion(folderRequest);
    }

    @GetMapping("/download/{idNotificacion}/{idDocumento}")
    public String downloadFile(@PathVariable("idNotificacion") String idNotificacion, @PathVariable("idDocumento") String idDocumento, Authentication authentication) {

        var idCasilla = ((UserDetail) authentication.getPrincipal()).getIdCasilla();


        return documentosGeneralesService.download(idDocumento, idCasilla, idNotificacion);

    }

}
