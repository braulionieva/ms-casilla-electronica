package pe.gob.mpfn.casilla.notifications.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import pe.gob.mpfn.casilla.notifications.exception.InternalServerErrorException;
import pe.gob.mpfn.casilla.notifications.model.dto.rest.BandejaFiscalResponse;

import java.util.Objects;

@Service
@Slf4j
public class BandejaFiscalClientService {

    @Value("${endpoint.bandejaFiscal}")
    private String url;

    public boolean esAbogado(String dni) {
        RestClient restClient = RestClient.create("%s/%s".formatted(url, dni));
        ResponseEntity<BandejaFiscalResponse> response = null;
        try {
            response = restClient.get()
                    .retrieve().toEntity(BandejaFiscalResponse.class);
            return Objects.requireNonNull(response.getBody()).code() == HttpStatus.OK.value();

        } catch (HttpClientErrorException | NullPointerException e) {
            log.info("Error al invocar el servicio: %s - %s".formatted(url, e.getMessage()));
            throw new InternalServerErrorException();
        }
    }
}
