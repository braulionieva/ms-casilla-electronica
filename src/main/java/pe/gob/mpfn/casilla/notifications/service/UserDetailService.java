package pe.gob.mpfn.casilla.notifications.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pe.gob.mpfn.casilla.notifications.model.user.UserDetail;
import pe.gob.mpfn.casilla.notifications.repository.AccountRepository;

import static pe.gob.mpfn.casilla.notifications.util.enums.AppParams.ACTIVE_ACCOUNT;
import static pe.gob.mpfn.casilla.notifications.util.enums.Messages.INVALID_USER;

@Service
public class UserDetailService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailService.class);
    private final AccountRepository accountRepository;

    public UserDetailService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String dni) throws UsernameNotFoundException {

        var optionalAcc = accountRepository.findFirstByUsuario(dni);
        var user = optionalAcc.orElseThrow(() -> {
            log.info("Uusario no encontrado o no existe");
            return new UsernameNotFoundException(INVALID_USER.getValue());
        });

        if (!ACTIVE_ACCOUNT.getValue().equals(user.getEstado())) {
            //throw new InactiveAccountException(INACTIVE_ACCOUNT_MESSAGE.getValue());
        }
        return UserDetail.builder()
                .userName(user.getUsuario())
                .password(user.getClave())
                .dni(user.getUsuario())
                .idPersona(user.getIdPerona())
                .tipoCasilla(user.getTipoCasilla())
                .idCasilla(user.getId())
                .build();
    }


}
