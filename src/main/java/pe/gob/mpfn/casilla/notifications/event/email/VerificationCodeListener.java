package pe.gob.mpfn.casilla.notifications.event.email;

import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pe.gob.mpfn.casilla.notifications.exception.EmailServiceException;
import pe.gob.mpfn.casilla.notifications.service.EmailService;

import java.io.IOException;

@Slf4j
@Component
public class VerificationCodeListener {

    private final EmailService emailService;

    public VerificationCodeListener(EmailService emailService) {
        this.emailService = emailService;
    }


    @EventListener
    public void onCodeCreated(VerificationCodeEvent event) {
        try {
            emailService.sendCodigoVerificacionEmail(event);
        } catch (IOException | TemplateException e) {
            log.error("Error al enviar mensaje al usuario", e);
            throw new EmailServiceException(e.getMessage(), e);
        }

    }
}
