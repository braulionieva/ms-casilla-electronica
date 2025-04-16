package pe.gob.mpfn.casilla.notifications.repository;

import pe.gob.mpfn.casilla.notifications.model.dto.SecurityResponseDto;
import pe.gob.mpfn.casilla.notifications.model.dto.VerificationCode2FARequest;

public interface SecurityRepository {

    SecurityResponseDto generateVerificationCode2FA(VerificationCode2FARequest datos);
    SecurityResponseDto validateVerificationCode2FA(VerificationCode2FARequest datos);
    SecurityResponseDto registerFailedAttempt(VerificationCode2FARequest datos);
    SecurityResponseDto checkAccount(VerificationCode2FARequest datos);
    void restartAttempts(String usuario, String idCasilla, int idFlujoCasilla);

}