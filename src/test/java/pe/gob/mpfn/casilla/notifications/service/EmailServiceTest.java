package pe.gob.mpfn.casilla.notifications.service;

import freemarker.template.TemplateException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pe.gob.mpfn.casilla.notifications.event.email.*;
import pe.gob.mpfn.casilla.notifications.model.dto.AffiliateResult;

import java.io.IOException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class EmailServiceTest {

    @Autowired
    EmailService emailService;
    static final String EMAIL = "pcruzd@mpfn.gob.pe";


    @Test
    void activatedAccount() {
        var rq = new ActiveAccountEvent(
                "NOMBRE",
                "2131232",
                EMAIL
        );
        assertDoesNotThrow(() -> emailService.activatedAccount(rq));
    }

    @Test
    void sendForgotPasswordEmail()  {
        ForgotPasswordEvent event = new ForgotPasswordEvent(
                "Test user",
                EMAIL,
                "http://mpfn.com/reset"
        );
        assertDoesNotThrow(() -> emailService.sendForgotPasswordEmail(event));
    }

    @Test
    void sendResetPasswordEmail()  {
        ResetPasswordEvent event = new ResetPasswordEvent(
                "Test User",
                "213123",
                EMAIL
        );
        assertDoesNotThrow(() -> emailService.sendResetPasswordEmail(event));
    }


    @Test
    void sendPasswordUpdatedEmail()  {
        var rq = new UpdatePasswordEvent(
                "FULLNAME", EMAIL
        );
        assertDoesNotThrow(() -> emailService.sendPasswordUpdatedEmail(rq));
    }

    @Test
    void sendChangeEmailRequestEmail() {
        ChangeEmailRequestEvent event = new ChangeEmailRequestEvent(
                "Test User",
                "SDWWRS",
                EMAIL
        );
        assertDoesNotThrow(() -> emailService.sendChangeEmailRequestEmail(event));
    }

    @Test
    void sendPasswordUpdatedReminder() {

            var result = new AffiliateResult(EMAIL, "23232", "Manuel Pedro", "Cruz", "Daza", new Date());
        assertDoesNotThrow(() -> emailService.sendPasswordUpdatedReminder(result));

    }
}