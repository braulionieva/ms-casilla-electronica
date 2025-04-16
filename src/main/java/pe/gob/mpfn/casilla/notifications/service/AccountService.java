package pe.gob.mpfn.casilla.notifications.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pe.gob.mpfn.casilla.notifications.event.email.UpdatePasswordEvent;
import pe.gob.mpfn.casilla.notifications.exception.AffiliateNotFoundException;
import pe.gob.mpfn.casilla.notifications.exception.HttpStatusResponseException;
import pe.gob.mpfn.casilla.notifications.model.dto.*;
import pe.gob.mpfn.casilla.notifications.model.user.UserDetail;
import pe.gob.mpfn.casilla.notifications.repository.AbogadoRepository;
import pe.gob.mpfn.casilla.notifications.repository.AccountRepository;
import pe.gob.mpfn.casilla.notifications.repository.AffiliateRepository;
import pe.gob.mpfn.casilla.notifications.repository.TokenRepository;
import pe.gob.mpfn.casilla.notifications.security.AuthenticationRequest;
import pe.gob.mpfn.casilla.notifications.security.LoginResponse;
import pe.gob.mpfn.casilla.notifications.util.PwCryptUtils;
import pe.gob.mpfn.casilla.notifications.util.StringUtils;
import pe.gob.mpfn.casilla.notifications.util.enums.*;

import static pe.gob.mpfn.casilla.notifications.util.enums.Messages.AFFILIATE_NOT_FOUND;

@Service
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final AffiliateRepository affiliateRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final AbogadoRepository abogadoRepository;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;

    public AccountService(AccountRepository accountRepository, AffiliateRepository affiliateRepository, ApplicationEventPublisher eventPublisher, AbogadoRepository abogadoRepository, JwtService jwtService, TokenRepository tokenRepository) {
        this.accountRepository = accountRepository;
        this.affiliateRepository = affiliateRepository;
        this.eventPublisher = eventPublisher;
        this.abogadoRepository = abogadoRepository;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
    }

    public AccountRecord getUserAccountData(String dni) {
        var user = affiliateRepository.getAccountData(new AuthenticationRequest(dni))
                .orElseThrow(() -> new UsernameNotFoundException(AFFILIATE_NOT_FOUND.getValue()));
        if (TiposPersona.NATURAL.getValue().equals(user.getTipoUsr())
                && AppParams.ES_ABOGADO.getValue().equals(user.getAbogado())) {

            var abogadoOpt = abogadoRepository.findFirstByIdPersonaAndEsAbogado(user.getIdPersona(), AppParams.ES_ABOGADO.getValue());
            abogadoOpt.ifPresent(user::setOtros);
        }
        return user;
    }

    public ValidateCodeResponse updatePassword(String dni, UpdatePasswordRequest request) {
        var oldPw = PwCryptUtils.decrypt(request.oldPw());
        var pw = PwCryptUtils.decrypt(request.pw());
        var user = affiliateRepository.getAccountData(new AuthenticationRequest(dni, oldPw, "", "", "", ""))
                .orElseThrow(() -> new AffiliateNotFoundException("Contraseña incorrecta."));

        try {
            affiliateRepository.updatePassword(
                    new UpdatePasswordRequest(user.getNumDoc(), pw, oldPw, "","",null)
            );
        } catch (HttpStatusResponseException e) {
            throw new AffiliateNotFoundException("Contraseña incorrecta.");
        }

        var fullName = "%s %s %s".formatted(
                user.getNombre(), user.getApePat(), user.getApeMat()
        );
        eventPublisher.publishEvent(new UpdatePasswordEvent(
                StringUtils.formatearNombre(fullName), user.getCorreo()));

        return new ValidateCodeResponse();
    }

    public ValidateCodeResponse updateProfile(UpdateProfileRequest request, String dni) {

        var accountRecord = getUserAccountData(dni);
        if (TiposPersona.NATURAL.getValue().equals(accountRecord.getTipoUsr()) && request.abogado()) {

            //    if (!bandejaFiscalClientService.esAbogado(accountRecord.getNumDoc())) {
            //        throw new HttpStatusResponseException(HttpStatus.BAD_REQUEST, "Usted no cuenta con apersonamiento de casos fiscales.");
            //    }
            // doble perfil para natural
            accountRepository.updateProfile(accountRecord, request);
            //
        } else {
            // TODO: JURÍDICA
        }
        return new ValidateCodeResponse();
    }

    public LoginResponse changeProfile(UserDetail userDetail, ChangeProfileRequest changeProfileRequest) {
        var user = affiliateRepository.getAccountData(new AuthenticationRequest(userDetail.getUsername()))
                .orElseThrow(() -> new HttpStatusResponseException(HttpStatus.BAD_REQUEST, Messages.SOLICITUD_INVALIDA.getValue()));

        var token = tokenRepository.findFirstByUserAndStatusOrderByCreateAtDesc(
                        user.getUsuario(), TokenStatus.VALID.getValue())
                .orElseThrow(() -> new HttpStatusResponseException(HttpStatus.BAD_REQUEST, Messages.SOLICITUD_INVALIDA.getValue()));


        if (!LoginType.isValidLoginType(changeProfileRequest.profile())) {
            log.error("Tipo de sesión inválida: %s".formatted(changeProfileRequest));
            throw new HttpStatusResponseException(HttpStatus.BAD_REQUEST, Messages.SOLICITUD_INVALIDA.getValue());
        }

        var jwtToken = jwtService.generateTokenOnChageProfile(user, changeProfileRequest.profile());
        token.setToken(jwtToken);
        tokenRepository.save(token);
        var userFullName = "%s %s %s".formatted(
                user.getNombre(), user.getApePat(), user.getApeMat()
        );
        return new LoginResponse(userFullName, user.getNumDoc(), jwtToken);

    }

    public ValidateCodeResponse validate2FA(UserDetail userDetail) {
        return affiliateRepository.validate2FA(userDetail);
    }

    public ValidateCodeResponse activate2FA(UserDetail userDetail, Manage2FARequest manage2FARequest) {
        return affiliateRepository.activate2FA(userDetail, manage2FARequest);
    }

}