package pe.gob.mpfn.casilla.notifications.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.gob.mpfn.casilla.notifications.model.dto.*;
import pe.gob.mpfn.casilla.notifications.model.user.UserDetail;
import pe.gob.mpfn.casilla.notifications.security.LoginResponse;
import pe.gob.mpfn.casilla.notifications.service.*;

@RestController
@RequestMapping("account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final AuthenticationService authenticationService;
    private final MenuService menuService;
    private final AffiliateService affiliateService;

    @PostMapping("/updatePassword")
    @Operation(summary = "Actualizar la contraseña desde la sessión actualizar datos")
    public ValidateCodeResponse updatePassword(
            @RequestBody @Valid UpdatePasswordRequest request,
            Authentication authentication) {
        var dni = ((UserDetail) authentication.getPrincipal()).getDni();
        return accountService.updatePassword(dni, request);
    }

    @GetMapping()
    public AccountRecord getAccuntUserData(Authentication authentication) {
        var dni = ((UserDetail) authentication.getPrincipal()).getDni();
        return accountService.getUserAccountData(dni);
    }

    @PostMapping("/logout")
    public ResponseEntity<ValidateCodeResponse> invalidateToken(
            Authentication authentication) {
        var dni = ((UserDetail) authentication.getPrincipal()).getDni();
        authenticationService.invalidateToken(dni);
        return ResponseEntity.ok(new ValidateCodeResponse());
    }

    @GetMapping("/menu")
    public Object getMenu() {
        return menuService.getMenu();
    }

    @PostMapping("/send-email")
    @Operation(summary = "Envía un código de validación al nuevo correo cuando se intenta actualizar el correo electrónico actual")
    public ValidateCodeResponse sendEmail(@RequestParam("email") String email, Authentication authentication) {
        var dni = ((UserDetail) authentication.getPrincipal()).getDni();
        return affiliateService.sendChangeEmailRequest(dni, email);
    }

    @PostMapping("email")
    public ValidateCodeResponse updateEmail(
            @RequestBody UpdateEmailRequest updateEmailRequest,
            Authentication authentication
    ) {
        var dni = ((UserDetail) authentication.getPrincipal()).getDni();
        return affiliateService.changeEmail(updateEmailRequest, dni);
    }

    @PostMapping("update-profile")
    @Operation(summary = "")
    public ValidateCodeResponse updateProfile(@RequestBody UpdateProfileRequest request, Authentication authentication) {
        var dni = ((UserDetail) authentication.getPrincipal()).getDni();
        return accountService.updateProfile(request, dni);
    }

    @PostMapping("change")
    public LoginResponse changeProfile(Authentication authentication, @RequestBody ChangeProfileRequest changeProfileRequest) {

        return accountService.changeProfile((UserDetail) authentication.getPrincipal(), changeProfileRequest);
    }

    @GetMapping("2fa")
    public ValidateCodeResponse validate2FA(Authentication authentication) {
        return accountService.validate2FA((UserDetail) authentication.getPrincipal());
    }

    @PostMapping("2fa")
    public ValidateCodeResponse activate2FA(Authentication authentication, @RequestBody Manage2FARequest manage2FARequest) {
        return accountService.activate2FA((UserDetail) authentication.getPrincipal(), manage2FARequest);
    }

}
