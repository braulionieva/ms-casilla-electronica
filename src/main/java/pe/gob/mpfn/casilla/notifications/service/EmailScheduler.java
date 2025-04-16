package pe.gob.mpfn.casilla.notifications.service;

import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pe.gob.mpfn.casilla.notifications.model.dto.AffiliateResult;
import pe.gob.mpfn.casilla.notifications.repository.AccountRepository;

import java.io.IOException;

@Component
public class EmailScheduler {

    private static final Logger log = LoggerFactory.getLogger(EmailScheduler.class);
    private final AccountRepository accountRepository;
    private final EmailService emailService;

    public EmailScheduler(AccountRepository accountRepository, EmailService emailService) {
        this.accountRepository = accountRepository;
        this.emailService = emailService;
    }


    @Scheduled(cron = "0 0 12 * * ?")
    public void sendEmail() {
        var userList = accountRepository.usersWithInitialPassword();
        log.info("usuario a enviar el recordatorio: {}", userList.size());
        for (AffiliateResult user : userList) {
            try {
                emailService.sendPasswordUpdatedReminder(user);
            } catch (TemplateException | IOException e) {
                log.error("Error enviar el correo {} - {}", e.getMessage(), user.coUsuario());
            }
        }


    }

}
