package pe.gob.mpfn.casilla.notifications.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.gob.mpfn.casilla.notifications.model.dto.AccountRecord;
import pe.gob.mpfn.casilla.notifications.model.dto.ForgotPasswordRequest;
import pe.gob.mpfn.casilla.notifications.model.dto.ResentCodeDto;
import pe.gob.mpfn.casilla.notifications.model.dto.UpdatePasswordRequest;
import pe.gob.mpfn.casilla.notifications.model.dto.ValidateCodeResponse;
import pe.gob.mpfn.casilla.notifications.model.user.UserDetail;
import pe.gob.mpfn.casilla.notifications.security.AuthenticationRequest;
import pe.gob.mpfn.casilla.notifications.security.LoginResponse;
import pe.gob.mpfn.casilla.notifications.service.AffiliateService;
import pe.gob.mpfn.casilla.notifications.service.AuthenticationService;
import pe.gob.mpfn.casilla.notifications.service.RecuperaPasswordService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final AffiliateService affiliateService;
    private final RecuperaPasswordService recuperaPasswordService;

    public AuthController(
            AuthenticationService authenticationService,
            AffiliateService affiliateService,
            RecuperaPasswordService recuperaPasswordService
    ) {
        this.authenticationService = authenticationService;
        this.affiliateService = affiliateService;
        this.recuperaPasswordService = recuperaPasswordService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return authenticationService.authenticate(request);
    }


    @PostMapping("/updatePassword")
    @Operation(summary = "Cambiar la contraseña por primera vez")
    public ValidateCodeResponse updatePassword(
            @RequestBody @Valid UpdatePasswordRequest request,
            HttpSession session
    ) {
        return affiliateService.updatePassword(request);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Validar que el correo esté afiliado y enviar un correo de solicitud de contraseña")
    public ValidateCodeResponse forgotPassword(
            @RequestBody @Valid ForgotPasswordRequest request, HttpServletRequest info
    ) {
        //return recuperaPasswordService.validateAndSendEmail(request);
        return recuperaPasswordService.validateGenerateOtpAndSendEmail(request,info);
    }

    @PostMapping("/forgot-password-with-otp")
    @Operation(summary = "Validar que el correo esté afiliado y enviar un correo de solicitud de contraseña con código de verificación (otp) ")
    public ValidateCodeResponse forgotPasswordWithOtp(
            @RequestBody @Valid ForgotPasswordRequest request, HttpServletRequest info
    ) {
        return recuperaPasswordService.validateGenerateOtpAndSendEmail(request,info);
    }

    @PostMapping("check-application")
    @Operation(summary = "Verificar que el token de afiliación es válido para el cambio de contraseña")
    public ValidateCodeResponse checkApplicationToResetPassword(
            @RequestBody ForgotPasswordRequest request
    ) {
        return recuperaPasswordService.validateApplication(request);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Realizar el cambio de contraseña solicitado por el usuario")
    public ValidateCodeResponse resetPassword(
            @RequestBody @Valid UpdatePasswordRequest request
    ) {
        return recuperaPasswordService.resetPassword(request);
    }

    @GetMapping("/validate-security-response/{tipo}/{respuesta}/{token}")
    @Operation(summary = "Realizar la validación de la respuesta de seguridad remitida por el usuario")
    public boolean ValidateSecurityResponse(@PathVariable String tipo,@PathVariable String respuesta,@PathVariable String token,HttpServletRequest info) {

        return recuperaPasswordService.validarRespuestaSeguraConReniec(tipo,respuesta,token,info);
        //return true;
    }

    @PostMapping("/resent-code")
    @Operation(summary = "Realiza el reenvío de código de 2FA")
    public ValidateCodeResponse resetPassword(
            @RequestBody @Valid ResentCodeDto request
    ) {
        return authenticationService.resentCode(request);
    }

    @GetMapping("/bloquearcuenta/{token}")
    @Operation(summary = "Realizar la validación de la respuesta de seguridad remitida por el usuario")
    public ValidateCodeResponse bloquearCuenta(@PathVariable String token) {

        return recuperaPasswordService.bloqueoDeCuenta(token);
    }
}
