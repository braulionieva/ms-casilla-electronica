package pe.gob.mpfn.casilla.notifications.event.email;

import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pe.gob.mpfn.casilla.notifications.exception.EmailServiceException;
import pe.gob.mpfn.casilla.notifications.service.EmailService;

import java.io.IOException;

@Component
@Slf4j
public class AccountLockListener {

    public final EmailService emailService;

    public AccountLockListener(EmailService emailService) {
        this.emailService = emailService;
    }


    @EventListener
    public void onAccountLock(AccountLockEvent accountLockEvent) {

        try {
            emailService.notifyAccountLock(accountLockEvent);
        } catch (IOException | TemplateException e) {
            log.error("Error al enviar mensaje al usuario", e);
            throw new EmailServiceException(e.getMessage(), e);
        }


    }
}