package pe.gob.mpfn.casilla.notifications.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.mpfn.casilla.notifications.exception.HttpStatusResponseException;
import pe.gob.mpfn.casilla.notifications.model.dto.AccountRecord;
import pe.gob.mpfn.casilla.notifications.model.entity.MovimientoCasilla;
import pe.gob.mpfn.casilla.notifications.repository.AccountRepository;
import pe.gob.mpfn.casilla.notifications.repository.MovimientoCasillaRepository;
import pe.gob.mpfn.casilla.notifications.util.enums.LoginStatus;

import java.time.LocalDateTime;

@Service
@Slf4j
public class MovimientoCasillaService {

    private final MovimientoCasillaRepository movimientoCasillaRepository;
    private final AccountRepository accountRepository;

    public MovimientoCasillaService(MovimientoCasillaRepository movimientoCasillaRepository, AccountRepository accountRepository) {
        this.movimientoCasillaRepository = movimientoCasillaRepository;
        this.accountRepository = accountRepository;
    }



    @Transactional
    public void createAndSaveFirstLogin(AccountRecord userDetail) {

        var account = accountRepository.findById(userDetail.getCuentaId())
                .orElseThrow(() -> new HttpStatusResponseException(HttpStatus.BAD_REQUEST, "Usuario inválido"));

        log.info("1st login: cambio de estado USUARIO: %s".formatted(userDetail.getUsuario()));
        account.setFirstLogin(LoginStatus.FIRST_LOGIN.getValue());
        accountRepository.save(account);
        log.info("se registra en movimiento de primer login USUARIO: %s".formatted(userDetail.getUsuario()));
        var movimiento = new MovimientoCasilla();
        movimiento.setIdCasilla(userDetail.getCuentaId());
        movimiento.setTipoMovimientoCasilla(6);
        movimiento.setDescripcionMovimientoCasilla("REGISTRO PRIMER LOGIN");
        movimiento.setUsuarioCreacion(userDetail.getNumDoc());
        movimiento.setFechaCreacion(LocalDateTime.now());
        movimiento.setFechaMovimientoCasilla(LocalDateTime.now());
        movimiento.setEstadoMovimientoCasilla("1");
        movimientoCasillaRepository.save(movimiento);
    }
}
