package pe.gob.mpfn.casilla.notifications.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import pe.gob.mpfn.casilla.notifications.exception.HttpStatusResponseException;
import pe.gob.mpfn.casilla.notifications.exception.InternalServerErrorException;
import pe.gob.mpfn.casilla.notifications.model.dto.rest.ObtieneDocumentoResponse;
import pe.gob.mpfn.casilla.notifications.repository.NotificacionAdjuntoRepository;
import pe.gob.mpfn.casilla.notifications.util.DateUtils;

import java.util.Objects;

@Service
public class DocumentosGeneralesService {

    @Value("${endpoint.generalesDocumentos}")
    private String url;

    private final NotificacionAdjuntoRepository notificacionAdjuntoRepository;

    public DocumentosGeneralesService(NotificacionAdjuntoRepository notificacionAdjuntoRepository) {
        this.notificacionAdjuntoRepository = notificacionAdjuntoRepository;
    }

    public String download(String idDocumento, String idCasilla, String idNotificacion) {

        var documentInfo = notificacionAdjuntoRepository.obtenerAdjunto(idCasilla, idNotificacion, idDocumento)
                .orElseThrow(() -> new HttpStatusResponseException(HttpStatus.NOT_FOUND, "El documento no existe"));


        var isOlderThan180 = DateUtils.isDateOlderThan180Days(documentInfo.fechaCreacion());

        if (documentInfo.numeroOrden() != 1 && isOlderThan180) {
            throw new HttpStatusResponseException(HttpStatus.BAD_REQUEST, "El documento no se encuentra disponible");
        }

        RestClient restClient = RestClient.create("%s/%s".formatted(url, idDocumento));
        ResponseEntity<ObtieneDocumentoResponse> response = null;
        try {

            response = restClient.get()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve().toEntity(ObtieneDocumentoResponse.class);
            var list =  Objects.requireNonNull(response.getBody()).data();
            if (list.isEmpty()) {
                throw new HttpStatusResponseException(HttpStatus.NOT_FOUND, "El documento no existe");
            }

            return list.get(0).archivo();

        } catch (HttpClientErrorException | NullPointerException e) {
            throw new InternalServerErrorException();
        }

    }
}
