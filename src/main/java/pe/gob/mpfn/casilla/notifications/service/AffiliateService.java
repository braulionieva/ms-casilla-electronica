package pe.gob.mpfn.casilla.notifications.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.mpfn.casilla.notifications.event.email.ActiveAccountEvent;
import pe.gob.mpfn.casilla.notifications.event.email.ChangeEmailRequestEvent;
import pe.gob.mpfn.casilla.notifications.exception.HttpStatusResponseException;
import pe.gob.mpfn.casilla.notifications.model.dto.*;
import pe.gob.mpfn.casilla.notifications.repository.AccountRepository;
import pe.gob.mpfn.casilla.notifications.repository.AffiliateRepository;
import pe.gob.mpfn.casilla.notifications.repository.TokenRepository;
import pe.gob.mpfn.casilla.notifications.security.AuthenticationRequest;
import pe.gob.mpfn.casilla.notifications.util.PwCryptUtils;
import pe.gob.mpfn.casilla.notifications.util.StringUtils;
import pe.gob.mpfn.casilla.notifications.util.enums.LoginStatus;
import pe.gob.mpfn.casilla.notifications.util.enums.TokenStatus;

import static pe.gob.mpfn.casilla.notifications.util.enums.Messages.INVALID_USER;

@Service
@Slf4j
public class AffiliateService {

    private final AffiliateRepository affiliateRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final SMSService smsService;
    private final TokenRepository tokenRepository;
    private final AccountRepository accountRepository;

    public AffiliateService(AffiliateRepository affiliateRepository, ApplicationEventPublisher eventPublisher, SMSService smsService, TokenRepository tokenRepository, AccountRepository accountRepository) {
        this.affiliateRepository = affiliateRepository;
        this.eventPublisher = eventPublisher;
        this.smsService = smsService;
        this.tokenRepository = tokenRepository;
        this.accountRepository = accountRepository;
    }


    public ValidateCodeResponse sendChangeEmailRequest(String dni, String email) {

        var user = affiliateRepository.getAccountData(new AuthenticationRequest(dni))
                .orElseThrow(() -> new HttpStatusResponseException(HttpStatus.BAD_REQUEST, "No se puede actualizar el correo en estos momentos."));
        var result = smsService.generateCode(new SmsGenerateCodeRequest(email, "", user.getNumDoc()), "");
        eventPublisher.publishEvent(new ChangeEmailRequestEvent(result.nombreCompleto(), result.smsCode(), email));

        return new ValidateCodeResponse();

    }

    @Transactional
    public ValidateCodeResponse updatePassword(UpdatePasswordRequest request) {

        var tokenOptional = tokenRepository.findFirstByUserAndStatusOrderByCreateAtDesc(request.userId(), TokenStatus.VALID.getValue());

        if (tokenOptional.isEmpty() || !tokenOptional.get().getToken().equals(request.token())) {
            log.info("%s - solicitud de actualización de contraseña inválida".formatted(request.userId()));
            throw new HttpStatusResponseException(HttpStatus.BAD_REQUEST, "Solicitud inválida");
        }

        var user = affiliateRepository.getAccountData(new AuthenticationRequest(request.userId(), PwCryptUtils.decrypt(request.oldPw()), "", "", "", ""))
                .orElseThrow(() -> new HttpStatusResponseException(HttpStatus.BAD_REQUEST, "Usuario inválido"));

        affiliateRepository.updatePassword(new UpdatePasswordRequest(
                request.userId(),
                PwCryptUtils.decrypt(request.pw()),
                PwCryptUtils.decrypt(request.oldPw()),
                request.token()
        ));
        var account = accountRepository.findById(user.getCuentaId())
                .orElseThrow(() -> new HttpStatusResponseException(HttpStatus.BAD_REQUEST, "Usuario inválido"));

        log.info("Cambio de contraseña por 1ra vez: %s".formatted(request.userId()));
        account.setFirstLogin(LoginStatus.FIRST_PASSWORD.getValue());
        accountRepository.save(account);
        var fullName = "%s %s %s".formatted(user.getNombre(), user.getApePat(), user.getApeMat());
        eventPublisher.publishEvent(
                new ActiveAccountEvent(
                        StringUtils.formatearNombre(fullName),
                        user.getUsuario(), user.getCorreo()));
        return new ValidateCodeResponse(true, "");
    }

    public AccountRecord getAccountData(AuthenticationRequest request) {

        return affiliateRepository
                .getAccountData(request)
                .orElseThrow(() -> new UsernameNotFoundException(INVALID_USER.getValue()));

    }

    public ValidateCodeResponse changeEmail(UpdateEmailRequest updateEmailRequest, String dni) {

        smsService.validate(new SmsGenerateCodeRequest(updateEmailRequest.code(), updateEmailRequest.email()), "");
        var user = affiliateRepository.getAccountData(new AuthenticationRequest(dni))
                .orElseThrow(() -> new HttpStatusResponseException(HttpStatus.BAD_REQUEST, "No se puede actualizar el correo en estos momentos."));


        return affiliateRepository.updateEmail(updateEmailRequest, user.getCuentaId());

    }
}
