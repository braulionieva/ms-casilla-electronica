package pe.gob.mpfn.casilla.notifications.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pe.gob.mpfn.casilla.notifications.event.email.ForgotPasswordEvent;
import pe.gob.mpfn.casilla.notifications.event.email.ResetPasswordEvent;
import pe.gob.mpfn.casilla.notifications.event.email.VerificationCodeEvent;
import pe.gob.mpfn.casilla.notifications.exception.AffiliateNotFoundException;
import pe.gob.mpfn.casilla.notifications.exception.HttpStatusResponseException;
import pe.gob.mpfn.casilla.notifications.model.dto.*;
import pe.gob.mpfn.casilla.notifications.model.entity.SittCodigo;
import pe.gob.mpfn.casilla.notifications.repository.AccountRepository;
import pe.gob.mpfn.casilla.notifications.repository.AffiliateRepository;
import pe.gob.mpfn.casilla.notifications.repository.SecurityRepository2;
import pe.gob.mpfn.casilla.notifications.repository.SittCodigoRepository;
import pe.gob.mpfn.casilla.notifications.security.AuthenticationRequest;
import pe.gob.mpfn.casilla.notifications.util.*;
import pe.gob.mpfn.casilla.notifications.util.enums.AppParams;
import pe.gob.mpfn.casilla.notifications.util.enums.LoginStatus;

import java.time.LocalDate;
import java.util.Base64;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static pe.gob.mpfn.casilla.notifications.util.enums.AppParams.CODIGO_GENERADO_SIN_VALIDAR;
import static pe.gob.mpfn.casilla.notifications.util.enums.AppParams.CODIGO_GENERADO_VALIDADO;
import static pe.gob.mpfn.casilla.notifications.util.enums.Messages.INVALID_USER;

@Service
public class RecuperaPasswordService {

    private static final Logger log = LoggerFactory.getLogger(RecuperaPasswordService.class);
    private final GoogleRecaptchaService googleRecaptchaService;
    @Value("${app.recuperar_cuenta}")
    private String forgotPasswordUrl;

    private final JwtService jwtService;
    private final AffiliateRepository affiliateRepository;
    private final SittCodigoRepository sittCodigoRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final AccountRepository accountRepository;
    private final SecurityRepository2 securityRepository;
    private final ReniecService2 reniecService;


    public RecuperaPasswordService(JwtService jwtService, AffiliateRepository affiliateRepository, SittCodigoRepository sittCodigoRepository, ApplicationEventPublisher eventPublisher, AccountRepository accountRepository, GoogleRecaptchaService googleRecaptchaService, SecurityRepository2 securityRepository, ReniecService2 reniecService) {
        this.jwtService = jwtService;
        this.affiliateRepository = affiliateRepository;
        this.sittCodigoRepository = sittCodigoRepository;
        this.eventPublisher = eventPublisher;
        this.accountRepository = accountRepository;
        this.googleRecaptchaService = googleRecaptchaService;
        this.securityRepository = securityRepository;
        this.reniecService = reniecService;
    }

    public String getUrlToken(String dni) {
        var result = affiliateRepository
                .getAccountData(new AuthenticationRequest(dni, "", "", "", "", ""))
                .orElseThrow(() -> new UsernameNotFoundException(INVALID_USER.getValue()));

        var token = jwtService.generateTokenWithExpiration(
                result,
                AppParams.EXPIRACION_RECUPERACION_PASSWORD_TOKEN.getIntVal()
        );

        var dateNow = LocalDate.now();
        var local = UUID.randomUUID().toString();
        var codigo = new SittCodigo(
                null, "1", token,
                "::",
                CODIGO_GENERADO_SIN_VALIDAR.getValue()
                , dateNow, dateNow,
                dni
        );
        sittCodigoRepository.save(codigo);
        return "%s&uid=%s".formatted(
                Base64.getEncoder().encodeToString(token.getBytes()),
                local
        );
    }

    public ValidateCodeResponse validateAndSendEmail(ForgotPasswordRequest request) {

        //validar el recapctcha de google
        googleRecaptchaService.validaToken(request.token());

        //validar y obtener los datos de la cuenta del usuario
        var account = affiliateRepository.getAccountData(new AuthenticationRequest((request.user()), "", "","","",""))
                .orElseThrow(() -> new AffiliateNotFoundException("EL usuario ingresado no existe en el sistema"));


        //preparo datos para publicar en el kafka de envio de correo
        var fullname = "%s %s %s".formatted(account.getNombre(), account.getApePat(), account.getApeMat());
        var code = this.getUrlToken(request.user());
        var url = "%s%s".formatted(forgotPasswordUrl, code);
        //kafka de correo
        eventPublisher.publishEvent(new ForgotPasswordEvent(StringUtils.formatearNombre(fullname), account.getCorreo(), url));

        return new ValidateCodeResponse(true, HttpStatus.OK.value(), EmailUtils.obfuscateEmail(account.getCorreo()));
    }

    public ValidateCodeResponse validateGenerateOtpAndSendEmail(ForgotPasswordRequest request, HttpServletRequest info) {

        //validar el recapctcha de google
        googleRecaptchaService.validaToken(request.token());

        //validar y obtener los datos de la cuenta del usuario
        var account = affiliateRepository.getAccountData(new AuthenticationRequest((request.user()), "", "","","",""))
                .orElseThrow(() -> new AffiliateNotFoundException("EL usuario ingresado no existe en el sistema"));

        //validar si se encuentra bloqueada
        /*String estaBloqueadoLaCuenta = securityRepository.validarCuentaBloqueoFlujoCambioContrasena(request.user())
                .orElseThrow(() -> new AffiliateNotFoundException("EL usuario ingresado no existe en el sistema")); */

        String estaBloqueadoLaCuenta = "0";

        if (estaBloqueadoLaCuenta.equals("2")){

            return new ValidateCodeResponse(true, 2, "Usuario Bloqueado");

        }else{


        //obtener ip y navegador
        String clientIp = getClientIpAddress(info);
        String clientBrowser = getClientBrowser(info);

        //validar y obtener los datos de la cuenta del usuario
        var codigoVerificacion = securityRepository.generarCodigoVerificacion(new CodeVerificationValidation(account.getCuentaId(),1501L,clientIp,clientBrowser,request.user()))
                .orElseThrow(() -> new AffiliateNotFoundException("El código no fue generado correctamente"));

        log.info("el correo del usuario es : " + account.getCorreo());
        log.info("el correo del usuario ofuscado es : " + EmailUtils.obfuscateEmail(account.getCorreo()));
        log.info("el código de verificación es : " + codigoVerificacion);



        //preparo datos para publicar en el kafka de envio de correo
        var fullname = "%s %s %s".formatted(account.getNombre(), account.getApePat(), account.getApeMat());

        var code = this.getUrlToken(request.user());
        var url = "%s%s".formatted(forgotPasswordUrl, code);

        //kafka de correo para enviar código verificación
        eventPublisher.publishEvent(new VerificationCodeEvent(StringUtils.formatearNombre(fullname), codigoVerificacion,account.getCorreo()));

        //kafka de correo de recuperación de cuenta
        eventPublisher.publishEvent(new ForgotPasswordEvent(StringUtils.formatearNombre(fullname), account.getCorreo(), url));

        return new ValidateCodeResponse(true, HttpStatus.OK.value(), EmailUtils.obfuscateEmail(account.getCorreo()));

        }
    }



    public ValidateCodeResponse resetPassword(UpdatePasswordRequest request) {

        var resp = tokenExistsOrValid(request.token());
        var user = jwtService.extractUsername(resp.getCodigoGenerado());

        if (!resp.getCoUsuario().equals(user)) {
            log.info("Solicitud de cambio de contraseña inválida: {}", request.userId());
            throw new HttpStatusResponseException(NOT_FOUND, "Solicitud de cambio de contraseña inválida");
        }
        resp.setFeDModificacion(LocalDate.now());
        resp.setFlCIndicador(CODIGO_GENERADO_VALIDADO.getValue());
        sittCodigoRepository.save(resp);

        var userCas = affiliateRepository.getAccountData(new AuthenticationRequest(user))
                .orElseThrow(() -> new AffiliateNotFoundException("Usuario inválido"));


            affiliateRepository.updatePassword(
                    new UpdatePasswordRequest(user, PwCryptUtils.decrypt(request.pw()), "", "", PwCryptUtils.decrypt(request.codigoVerificacion()),request.idTipoVerificacion())
            );

            if (LoginStatus.REGISTERED.getValue().equals(userCas.getFirstLogin())) {
                var account = accountRepository.findById(userCas.getCuentaId())
                        .orElseThrow(() -> new HttpStatusResponseException(HttpStatus.BAD_REQUEST, "Usuario inválido"));
                account.setFirstLogin(LoginStatus.FIRST_PASSWORD.getValue());
                accountRepository.save(account);
            }


            eventPublisher.publishEvent(new ResetPasswordEvent("%s %s %s".formatted(
                    userCas.getNombre(), userCas.getApePat(), userCas.getApeMat()
            ), user, userCas.getCorreo()));

            return new ValidateCodeResponse(true, "Se cambió su contraseña con éxito.");



    }

    public ValidateCodeResponse validateApplication(ForgotPasswordRequest request) {

        tokenExistsOrValid(request.token());
        return new ValidateCodeResponse(true, "OK");

    }

    private SittCodigo tokenExistsOrValid(String token) {
        var decodedToken = new String(Base64.getDecoder().decode(token));
        var retrievedToken = sittCodigoRepository
                .findFirstByCodigoGeneradoAndFlCIndicador(
                        decodedToken,
                        CODIGO_GENERADO_SIN_VALIDAR.getValue())
                .orElseThrow(() -> new HttpStatusResponseException(NOT_FOUND, "No existe o ya fue validada"));

        try {
            var expired = jwtService.isTokenExpired(retrievedToken.getCodigoGenerado());
            if (expired) {
                log.info("token expirado para la solicitud de cambio de contraseña");
                throw new HttpStatusResponseException(NOT_FOUND, "");
            }
        } catch (ExpiredJwtException e) {
            throw new HttpStatusResponseException(NOT_FOUND, "La sesión ha caducado");
        }

        return retrievedToken;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");

        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }

        return ipAddress;
    }


    private String getClientBrowser(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    public boolean validarRespuestaSeguraConReniec(String tipo,String respuesta,String token, HttpServletRequest request){

        var resp = tokenExistsOrValid(token);
        var user = jwtService.extractUsername(resp.getCodigoGenerado());

         DniValidationResponse dniValidationResponse = reniecService.validateDocument(user,request);
        //DniValidationResponse dniValidationResponse = reniecService.validateDocument("44407662",request);



        return switch (tipo) {
            case  "1"-> dniValidationResponse.digitoVerificacion().equals(respuesta);
            case  "2"-> dniValidationResponse.apellidoMaternoMadre().equals(respuesta);
            case  "3"-> dniValidationResponse.apellidoMaternoPadre().equals(respuesta);
            default -> false;
        };
    }

    public ValidateCodeResponse bloqueoDeCuenta(String token) {

        var resp = tokenExistsOrValid(token);
        var user = jwtService.extractUsername(resp.getCodigoGenerado());

        if (!resp.getCoUsuario().equals(user)) {
            log.info("Solicitud de bloqueo de usuario : {}", user);
            throw new HttpStatusResponseException(NOT_FOUND, "Hubo un problema al tratar de bloquear la cuenta");
        }


        var userCas = affiliateRepository.getAccountData(new AuthenticationRequest(user))
                .orElseThrow(() -> new AffiliateNotFoundException("Usuario inválido"));


        var bloqueo = securityRepository.bloqueoDeCuenta(userCas.getCuentaId())
                .orElseThrow(() -> new AffiliateNotFoundException("Usuario no bloqueado"));

        if (LoginStatus.REGISTERED.getValue().equals(userCas.getFirstLogin())) {
            var account = accountRepository.findById(userCas.getCuentaId())
                    .orElseThrow(() -> new HttpStatusResponseException(HttpStatus.BAD_REQUEST, "Usuario inválido"));
            account.setFirstLogin(LoginStatus.FIRST_PASSWORD.getValue());
            accountRepository.save(account);
        }


            return new ValidateCodeResponse(true, "Se Bloqueo al usuario.");



    }
}
