package pe.gob.mpfn.casilla.notifications.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.mpfn.casilla.notifications.event.email.AccountLockEvent;
import pe.gob.mpfn.casilla.notifications.event.email.VerificationCode2FAEvent;
import pe.gob.mpfn.casilla.notifications.exception.HttpStatusResponseException;
import pe.gob.mpfn.casilla.notifications.model.dto.ResentCodeDto;
import pe.gob.mpfn.casilla.notifications.model.dto.SecurityResponseDto;
import pe.gob.mpfn.casilla.notifications.model.dto.ValidateCodeResponse;
import pe.gob.mpfn.casilla.notifications.model.dto.VerificationCode2FARequest;
import pe.gob.mpfn.casilla.notifications.repository.SecurityRepository;
import pe.gob.mpfn.casilla.notifications.security.AuthenticationRequest;
import pe.gob.mpfn.casilla.notifications.security.LoginResponse;
import pe.gob.mpfn.casilla.notifications.util.EmailUtils;
import pe.gob.mpfn.casilla.notifications.util.PwCryptUtils;
import pe.gob.mpfn.casilla.notifications.util.enums.LoginStatus;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static pe.gob.mpfn.casilla.notifications.util.enums.Messages.*;
import static pe.gob.mpfn.casilla.notifications.util.enums.ObjetosOracle.*;
import static pe.gob.mpfn.casilla.notifications.util.enums.ObjetosOracle.PI_V_CO_US_CREACION;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthenticationService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AffiliateService affiliateService;
    private final AuthTokenService authTokenService;
    private final MovimientoCasillaService movimientoCasillaService;
    private final ApplicationEventPublisher eventPublisher;
    private final GoogleRecaptchaService googleRecaptchaService;
    private final SecurityRepository securityRepository;

    @Transactional
    public ResponseEntity<LoginResponse> authenticate(AuthenticationRequest request) {

        var es2FARequest = request.code() != null;

        if (!es2FARequest) {
            googleRecaptchaService.validaToken(request.token());
        }

        var pw = PwCryptUtils.decrypt(request.password());
        var ip = PwCryptUtils.decrypt(request.ip());
        var dispositivo = PwCryptUtils.decrypt(request.dispositivo());

        VerificationCode2FARequest verificationCodeRequest = new VerificationCode2FARequest();
        verificationCodeRequest.setIdTipoVerificacion(TIPO_VERIFICACION_INICIO_SESION);
        verificationCodeRequest.setIpUsuario(ip);
        verificationCodeRequest.setDispositivo(dispositivo);
        verificationCodeRequest.setUsuario(request.usuario());
        verificationCodeRequest.setSoloBloqueo("1");

        SecurityResponseDto checkResponse = securityRepository.checkAccount(verificationCodeRequest);
        if (checkResponse.getCode().equals("2")) { //Cuenta bloqueada
            throw new HttpStatusResponseException(UNPROCESSABLE_ENTITY, checkResponse.getMessage());
        }

        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.usuario(),
                    pw
                )
            );
        } catch (AuthenticationException e) {
            log.info("Fallo autenticacion usuario: {}", request.usuario());
            // Registramos el intento fallido
            log.info("Registrando intento fallido para usuario paso 1: {}", request.usuario());
            securityRepository.registerFailedAttempt(verificationCodeRequest);
            verificationCodeRequest.setSoloBloqueo("0");
            checkResponse = securityRepository.checkAccount(verificationCodeRequest);
            System.out.println(checkResponse);
            if (checkResponse.getCode().equals("3")) { //Bloqueo de cuenta
                var userDetail = affiliateService.getAccountData(request);
                var userFullName = "%s %s %s".formatted(
                        userDetail.getNombre(), userDetail.getApePat(), userDetail.getApeMat()
                );
                eventPublisher.publishEvent(new AccountLockEvent(userFullName, userDetail.getCorreo()));
                throw new HttpStatusResponseException(UNPROCESSABLE_ENTITY, checkResponse.getMessage());
            } else {
                throw new HttpStatusResponseException(UNAUTHORIZED, BAD_CREDENTIALS_ATTEMPTS.getValue().replace("[ATTEMPTS]", String.valueOf(checkResponse.getRemainingAttempts())));
            }
        }

        var userDetail = affiliateService.getAccountData(request);

        var userFullName = "%s %s %s".formatted(
            userDetail.getNombre(), userDetail.getApePat(), userDetail.getApeMat()
        );

        log.info("Validandoooooooo: {}", userDetail.getTiene2fa());

        verificationCodeRequest.setIdCasilla(userDetail.getCuentaId());
        verificationCodeRequest.setUsuario(userDetail.getUsuario());

        if ("1".equals(userDetail.getTiene2fa().trim()) && es2FARequest) {
            // Valida codigo de verificacion
            log.info("Validando código de 2FA: USUARIO:: {}", userDetail.getUsuario());
            var code = PwCryptUtils.decrypt(request.code());
            verificationCodeRequest.setCode(code);
            SecurityResponseDto verificationCode = securityRepository.validateVerificationCode2FA(verificationCodeRequest);
            if (verificationCode.getVerificationResponse() == 0) {
                // Registramos el intento fallido
                log.info("Registrando intento fallido para usuario paso 2: {}", request.usuario());
                securityRepository.registerFailedAttempt(verificationCodeRequest);
                verificationCodeRequest.setSoloBloqueo("0");
                checkResponse = securityRepository.checkAccount(verificationCodeRequest);
                if (checkResponse.getCode().equals("3")) { //Bloqueo de cuenta
                    eventPublisher.publishEvent(new AccountLockEvent(userFullName, userDetail.getCorreo()));
                    throw new HttpStatusResponseException(UNPROCESSABLE_ENTITY, checkResponse.getMessage());
                } else {
                    throw new HttpStatusResponseException(HttpStatus.BAD_REQUEST, WRONG_VERIFICATION_CODE_ATTEMPTS.getValue().replace("[ATTEMPTS]", String.valueOf(checkResponse.getRemainingAttempts())));
                }
            }
        }

        if ( "1".equals(userDetail.getTiene2fa().trim()) && !es2FARequest ) {
            log.info("Enviando correo de 2FA: USUARIO:: {}", userDetail.getUsuario());
            SecurityResponseDto verificationCode = securityRepository.generateVerificationCode2FA(verificationCodeRequest);
            eventPublisher.publishEvent(new VerificationCode2FAEvent(userFullName, verificationCode.getVerificationCode(), verificationCode.getExpiration(), userDetail.getCorreo()));
            log.info("Necesida ingresar codigo 2FA: USUARIO: {}", userDetail.getUsuario());
            return ResponseEntity
                    .accepted()
                    .body(new LoginResponse(userDetail.getUsuario(), EmailUtils.obfuscateEmail(userDetail.getCorreo()), "Validar 2FA"));
        }

        var jwtToken = jwtService.generateToken(userDetail);
        authTokenService.saveToken(userDetail, jwtToken);

        if (LoginStatus.REGISTERED.getValue().equals(userDetail.getFirstLogin())) {
            log.info("Necesida cambio de password para ingresar: USUARIO: {}", userDetail.getUsuario());
            return ResponseEntity
                    .accepted()
                    .body(new LoginResponse(userDetail.getNombre(), userDetail.getNumDoc(), jwtToken));
        }

        var r = new LoginResponse(userFullName, userDetail.getNumDoc(), jwtToken);

        if (LoginStatus.FIRST_PASSWORD.getValue().equals(userDetail.getFirstLogin())) {
            log.info("1st login. Guardar movimiento - Usuario:: {}", userDetail.getUsuario());

            movimientoCasillaService.createAndSaveFirstLogin(userDetail);
        }

        securityRepository.restartAttempts(userDetail.getUsuario(), userDetail.getCuentaId(), TIPO_VERIFICACION_INICIO_SESION);

        return ResponseEntity.ok(r);
    }


    public void invalidateToken(String dni) {

        authTokenService.invalidateToken(dni);
    }

    public ValidateCodeResponse resentCode(ResentCodeDto request) {
        log.info("Usuario para reenviar codigo: USUARIO:: {}", request.getUsuario());

        var user = PwCryptUtils.decrypt(request.getUsuario());
        var ip = PwCryptUtils.decrypt(request.getIpUsuario());
        var dispositivo = PwCryptUtils.decrypt(request.getDispositivo());

        VerificationCode2FARequest verificationCodeRequest = new VerificationCode2FARequest();
        verificationCodeRequest.setIdTipoVerificacion(TIPO_VERIFICACION_INICIO_SESION);
        verificationCodeRequest.setIpUsuario(ip);
        verificationCodeRequest.setDispositivo(dispositivo);
        verificationCodeRequest.setUsuario(user);

        AuthenticationRequest auth = new AuthenticationRequest(user);
        var userDetail = affiliateService.getAccountData(auth);

        var userFullName = "%s %s %s".formatted(
            userDetail.getNombre(), userDetail.getApePat(), userDetail.getApeMat()
        );

        verificationCodeRequest.setIdCasilla(userDetail.getCuentaId());

        SecurityResponseDto verificationCode = securityRepository.generateVerificationCode2FA(verificationCodeRequest);
        eventPublisher.publishEvent(new VerificationCode2FAEvent(userFullName, verificationCode.getVerificationCode(), verificationCode.getExpiration(), userDetail.getCorreo()));
        log.info("Se reenvio codigo 2FA: USUARIO: {}", userDetail.getUsuario());

        return new ValidateCodeResponse(true, "Se ha reenviado el código satisfactoriamente");
    }

}