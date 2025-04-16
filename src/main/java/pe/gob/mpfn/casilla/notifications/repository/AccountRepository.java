package pe.gob.mpfn.casilla.notifications.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pe.gob.mpfn.casilla.notifications.model.entity.Account;
import pe.gob.mpfn.casilla.notifications.model.projection.AccountProjection;

import java.util.Optional;

@Repository
public interface AccountRepository extends CrudRepository<Account, String>, CustomAccountRepository {

    Optional<AccountProjection> findFirstByUsuario(String dni);

}
