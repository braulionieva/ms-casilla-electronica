package pe.gob.mpfn.casilla.notifications.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import pe.gob.mpfn.casilla.notifications.exception.HttpStatusResponseException;
import pe.gob.mpfn.casilla.notifications.exception.InternalServerErrorException;
import pe.gob.mpfn.casilla.notifications.model.dto.rest.ReCaptchaResponse;

import java.util.Objects;

@Service
public class GoogleRecaptchaService {

    private static final Logger log = LoggerFactory.getLogger(GoogleRecaptchaService.class);
    @Value("${endpoint.recaptcha.url}")
    private String url;


    public void validaToken(String token) {

        RestClient restClient = RestClient.create("%s&response=%s".formatted(url, token));
        ResponseEntity<ReCaptchaResponse> response;
        try {
            response = restClient.get()
                    .retrieve().toEntity(ReCaptchaResponse.class);
            log.info("response: {}", response.getBody());
            if (!Objects.requireNonNull(response.getBody()).success()) {
                log.info("Recaptcha inválido");
                throw new HttpStatusResponseException(HttpStatus.BAD_REQUEST, "Captcha inválido.");
            }

        } catch (HttpClientErrorException | NullPointerException e) {
            log.info("Error: ", e);
            throw new InternalServerErrorException();
        }

    }
}
