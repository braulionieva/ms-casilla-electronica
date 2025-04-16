package pe.gob.mpfn.casilla.notifications.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pe.gob.mpfn.casilla.notifications.exception.HttpStatusResponseException;
import pe.gob.mpfn.casilla.notifications.model.dto.ForgotPasswordRequest;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class RecuperaPasswordServiceTest {

    @Autowired
    private RecuperaPasswordService recuperaPasswordService;

    @Test
    void getUrlToken() {
        assertThrows(HttpStatusResponseException.class, () -> {
            recuperaPasswordService.getUrlToken("46345234");

        });

    }


    @Test
    void validateAndSendEmail() {
        var request = new ForgotPasswordRequest("46345234", "");
        assertThrows(HttpStatusResponseException.class, () -> {
            recuperaPasswordService.validateAndSendEmail(request);
        });
    }
}