package pe.gob.mpfn.casilla.notifications.service;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pe.gob.mpfn.casilla.notifications.model.dto.DniValidationResponse;
import pe.gob.mpfn.casilla.notifications.model.dto.rest.ClientDniRequest;
import pe.gob.mpfn.casilla.notifications.model.dto.rest.ReniecDniResponse2;
import ua_parser.Client;
import ua_parser.Parser;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.HttpStatus;
import pe.gob.mpfn.casilla.notifications.exception.HttpStatusResponseException;

@Service
public class ReniecService2 {

    private static final Logger log = LoggerFactory.getLogger(ReniecService2.class);

    @Value("${endpoint.reniec}")
    private String url;

    @Value("${reniec.ip}")
    private String ip;

    public DniValidationResponse validateDocument(String numeroDocumento, HttpServletRequest request) {

        var request1 = getClientInfo(request);
        request1.setIp(request1.getClienteIp());
        request1.setNumeroDocumento(numeroDocumento);

        log.info("Url a utilizar: ", url);

        RestClient restClient = RestClient.create(url);
        ResponseEntity<ReniecDniResponse2> response = null;
        try {
            response = restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request1)
                    .retrieve().toEntity(ReniecDniResponse2.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.UNPROCESSABLE_ENTITY)) {
                log.info(e.getMessage());
                throw new HttpStatusResponseException(HttpStatus.NOT_FOUND, "El número de DNI es inválido.");
            }
            log.info("Error: ", e);
            throw new HttpStatusResponseException(HttpStatus.FAILED_DEPENDENCY, "El servicio del RENIEC no se encuentra disponible en este momento. Por favor inténtelo nuevamente más tarde.");
        }
        var body = response.getBody();
        if (response.getStatusCode() != HttpStatus.OK || body == null) {
            log.info("Error: {}", response.getStatusCode());
            throw new HttpStatusResponseException(HttpStatus.FAILED_DEPENDENCY, "El servicio del RENIEC no se encuentra disponible en este momento. Por favor inténtelo nuevamente más tarde.");
        }
        var ok = false;
        ok = numeroDocumento.equals(body.numeroDocumento());

        if (!ok) {
            log.info("Datos inválidos: {}", numeroDocumento);
            throw new HttpStatusResponseException(HttpStatus.BAD_REQUEST, "Los datos ingresados son inválidos. Por favor, intente nuevamente.");
        }


        return new DniValidationResponse(
                body.digitoVerificacion(),
                body.nombrePadre(),
                body.nombreMadre(),
                body.apellidoMaternoMadre(),
                body.apellidoMaternoPadre(),
                body.apellidoPaternoMadre(),
                body.apellidoPaternoPadre()
        );
    }

    public ClientDniRequest getClientInfo(HttpServletRequest request)  {
        var clientInfo = new ClientDniRequest();

        String clientIp = request.getHeader("X-Real-IP");
        log.info("Se obtiene real-ip {}", clientIp);
        if (clientIp == null || clientIp.isEmpty()) {
            log.info("X-Real-IP en null, se manda por defecto: {}", request.getRemoteAddr());
            clientIp = request.getRemoteAddr();
        }
        clientInfo.setClienteIp(clientIp);
        clientInfo.setHttpHost(request.getHeader("Host"));
        String userAgent = request.getHeader("User-Agent");
        clientInfo.setHttpUserAgent(userAgent);

        Parser uaParser = new Parser();
        Client client = uaParser.parse(userAgent);
        clientInfo.setTypeBrowser(client.userAgent.family);
        clientInfo.setBrowser(client.userAgent.family);
        clientInfo.setVersionBrowser(client.userAgent.major + "." + client.userAgent.minor);
        clientInfo.setParent(client.userAgent.family + " " + client.userAgent.major);
        clientInfo.setPlatform(client.os.family);
        clientInfo.setPlatformVersion(client.os.major + "." + (client.os.minor != null ? client.os.minor : "0"));
        return clientInfo;
    }






}
