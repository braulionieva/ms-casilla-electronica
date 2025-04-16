package pe.gob.mpfn.casilla.notifications.repository;

import org.springframework.data.jdbc.repository.query.Query;
import pe.gob.mpfn.casilla.notifications.model.dto.AccountRecord;
import pe.gob.mpfn.casilla.notifications.model.dto.AffiliateResult;
import pe.gob.mpfn.casilla.notifications.model.dto.UpdateProfileRequest;

import java.util.List;

public interface CustomAccountRepository {

    void updateProfile(AccountRecord account, UpdateProfileRequest request);

    List<AffiliateResult> usersWithInitialPassword();

    String encodedPw(CharSequence rawPassword);
}
