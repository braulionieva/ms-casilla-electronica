package pe.gob.mpfn.casilla.notifications.service;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import pe.gob.mpfn.casilla.notifications.event.email.*;
import pe.gob.mpfn.casilla.notifications.model.dto.AffiliateResult;
import pe.gob.mpfn.casilla.notifications.model.dto.CorreoDto;
import pe.gob.mpfn.casilla.notifications.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static pe.gob.mpfn.casilla.notifications.util.enums.AppParams.*;

@Service
@Slf4j
public class EmailService {

    @Value("${endpoint.link_casilla}")
    private String linkCasilla;

    @Value("${app.link_recuperar_cuenta}")
    private String linkRecuperarCuenta;

    private final Configuration configuration;
    private final KafkaTemplate<Integer, CorreoDto> kafkaTemplate;

    public EmailService(Configuration configuration, KafkaTemplate<Integer, CorreoDto> kafkaTemplate) {
        this.configuration = configuration;
        this.kafkaTemplate = kafkaTemplate;
    }


    /**
     * Cambio de contraseña la primera vez
     */
    public void activatedAccount(ActiveAccountEvent event) throws TemplateException, IOException {
        var dateTime = LocalDateTime.now();
        log.info("envío de cambio de contraseña 1ra vez : %s".formatted(event.recipient()));
        Map<String, Object> model = Map.of(
                "cuentaId", event.cuentaId(),
                "fullname", event.fullname(),
                "date", dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                "time", dateTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                "link_casilla", linkCasilla
        );
        String emailBody = compile("auth/cuenta-activada.html", model);
        sendEmail(event.recipient(), CUENTA_ACTIVADA_SUBJECT.getValue(), emailBody);
    }

    public void sendForgotPasswordEmail(ForgotPasswordEvent event) throws TemplateException, IOException {
        Map<String, Object> model = Map.of(
                "url", event.url(),
                "correo", event.email(),
                "fullname", event.fullname()
        );
        log.info("Envio solicitud de cambio de contraseña: %s".formatted(event.email()));
        String emailBody = compile("auth/solicitud-cambio-password.html", model);
        sendEmail(event.email(), SOLICITUD_CAMBIO_PASSWORD_SUBJECT.getValue(), emailBody);
    }

    public void sendCodigoVerificacionEmail(VerificationCodeEvent event) throws TemplateException, IOException {
        Map<String, Object> model = Map.of(
                "codigo", event.codigo(),
                "fullname", event.fullname()
        );
        log.info("Envio solicitud de codigo de verificación: %s".formatted(event.email()));
        String emailBody = compile("auth/solicitud-codigo-verificacion.html", model);
        sendEmail(event.email(), SOLICITUD_CODIGO_VERIFICACION_SUBJECT.getValue(), emailBody);
    }

    public void sendResetPasswordEmail(ResetPasswordEvent event) throws TemplateException, IOException {
        Map<String, Object> model = Map.of(
                "usuario", event.cuentaId(),
                "fullname", StringUtils.formatearNombre(event.fullname())
        );
        log.info("Enviando correo por reset password  %s".formatted(event.recipient()));
        String emailBody = compile("auth/reset-password.html", model);
        sendEmail(event.recipient(), CUENTA_ACTIVADA_SUBJECT.getValue(), emailBody);

    }

    public void sendPasswordUpdatedEmail(UpdatePasswordEvent event) throws TemplateException, IOException {
        var dateTime = LocalDateTime.now();
        Map<String, Object> model = Map.of(
                "fullname", event.fullname(),
                "date", dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                "time", dateTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                "link_casilla", linkCasilla
        );
        log.info("correo - actualizar contraseña: %s".formatted(event.recipient()));
        String emailBody = compile("account/update-password.html", model);
        sendEmail(event.recipient(), CUENTA_ACTIVADA_SUBJECT.getValue(), emailBody);
    }

    public void sendChangeEmailRequestEmail(ChangeEmailRequestEvent event) throws TemplateException, IOException {
        Map<String, Object> model = Map.of(
                "fullname", StringUtils.formatearNombre(event.fullname()),
                "code", event.code()
        );
        log.info("Correo colicitud de cambio de contraseña: %s".formatted(event.recipient()));
        String emailBody = compile("account/change-email-request.html", model);
        sendEmail(event.recipient(), CUENTA_ACTIVADA_SUBJECT.getValue(), emailBody);
    }

    public void sendPasswordUpdatedReminder(AffiliateResult event) throws TemplateException, IOException {
        var fullName = "%s %s %s".formatted(event.nombre(), event.apPat(), event.apMat());
        Map<String, Object> model = Map.of(
                "fullname", StringUtils.formatearNombre(fullName),
                "link_casilla", linkCasilla
        );
        log.info("Correo recodatorio de cambio de contraseña inicial %s".formatted(event.correo()));
        String emailBody = compile("account/update-password-reminder.html", model);
        sendEmail(event.correo(), CUENTA_ACTIVADA_SUBJECT.getValue(), emailBody);
    }

    /**
     * Enviar código de verificación 2FA
     */
    public void sendVerificationCode2FA(VerificationCode2FAEvent event) throws TemplateException, IOException {
        Map<String, Object> model = Map.of(
            "fullname", StringUtils.formatearNombre(event.fullname()),
            "code", event.code(),
            "expiration", event.expiration(),
            "url", linkRecuperarCuenta
        );
        log.info("Correo con código de verificación 2FA para iniciar sesión %s".formatted(event.recipient()));
        String emailBody = compile("auth/doble-factor.html", model);
        sendEmail(event.recipient(), SOLICITUD_2FA_SUBJECT.getValue(), emailBody);
    }

    /**
     * Notificar bloqueo de cuenta
     */
    public void notifyAccountLock(AccountLockEvent event) throws TemplateException, IOException {
        Map<String, Object> model = Map.of(
            "fullname", StringUtils.formatearNombre(event.fullname()),
            "url", linkRecuperarCuenta
        );
        log.info("Notificación de bloqueo de cuenta %s".formatted(event.email()));
        String emailBody = compile("account/cuenta-bloqueada.html", model);
        sendEmail(event.email(), ACCOUNT_LOCK_SUBJECT.getValue(), emailBody);
    }

    private void sendEmail(String recipient, String subject, String body) {
        kafkaTemplate.sendDefault(new CorreoDto(
                subject,
                recipient,
                body
        ));
    }

    private String compile(String template, Map<String, Object> model)
            throws IOException, TemplateException {
        return FreeMarkerTemplateUtils.processTemplateIntoString(
                configuration.getTemplate(template), model);
    }

}
