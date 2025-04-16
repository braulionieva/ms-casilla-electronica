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
import pe.gob.mpfn.casilla.notifications.model.dto.rest.ReniecDniRequest;
import pe.gob.mpfn.casilla.notifications.model.dto.rest.ReniecDniResponse;


@Service
public class ReniecService {

    private static final Logger log = LoggerFactory.getLogger(ReniecService.class);

    @Value("${endpoint.reniec}")
    private String url;

    @Value("${reniec.ip}")
    private String ip;

    private final RestClient restClient;

    public ReniecService() {
        this.restClient = RestClient.builder()
                .baseUrl("http://cfms-generales-persona-cliente-api-development.apps.dev.ocp4.cfe.mpfn.gob.pe")
                .build();
    }

    public DniValidationResponse validateDocument(String numeroDocumento, HttpServletRequest request) {

        ReniecDniRequest datosIngreso = new ReniecDniRequest();
        datosIngreso.setIp("201.240.68.38");
        datosIngreso.setNumeroDocumento(numeroDocumento);
        datosIngreso.setUsuarioConsulta("32920589");
        datosIngreso.setHttpHost("cfe.mpfn.gob.pe");

        ResponseEntity<ReniecDniResponse> response = restClient.post()
                .uri("/cfe/generales/persona/v1/e/personanatural/consulta/general")
                .contentType(MediaType.APPLICATION_JSON)
                .body(datosIngreso)
                .retrieve()
                .toEntity(ReniecDniResponse.class);


        return new DniValidationResponse(
                response.getBody().digitoVerificacion(),
                response.getBody().nombrePadre(),
                response.getBody().nombreMadre(),
                response.getBody().apellidoMaternoMadre(),
                response.getBody().apellidoMaternoPadre(),
                response.getBody().apellidoPaternoMadre(),
                response.getBody().apellidoPaternoPadre()
        );
    }




}
