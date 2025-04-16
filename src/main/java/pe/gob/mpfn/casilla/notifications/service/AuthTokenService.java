package pe.gob.mpfn.casilla.notifications.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.stereotype.Service;
import pe.gob.mpfn.casilla.notifications.model.dto.AccountRecord;
import pe.gob.mpfn.casilla.notifications.model.entity.AuthToken;
import pe.gob.mpfn.casilla.notifications.repository.TokenRepository;

import java.util.Date;

import static pe.gob.mpfn.casilla.notifications.util.enums.TokenStatus.REVOKED;
import static pe.gob.mpfn.casilla.notifications.util.enums.TokenStatus.VALID;

@Service
@RequiredArgsConstructor
public class AuthTokenService {

    private final TokenRepository tokenRepository;
    private final JdbcAggregateTemplate jdbcAggregateTemplate;

    public void saveToken(AccountRecord userDetail, String jwtToken) {

        var authToken = AuthToken.builder()
                .token(jwtToken)
                .user(userDetail.getUsuario())
                .createAt(new Date())
                .status(VALID.getValue())
                .build();
        jdbcAggregateTemplate.insert(authToken);

    }

    public void invalidateToken(String dni) {
        var optional = tokenRepository.findFirstByUserAndStatusOrderByCreateAtDesc(dni, VALID.getValue());
        optional.ifPresent(token -> {
            token.setStatus(REVOKED.getValue());
            tokenRepository.save(token);
        });
    }

}
