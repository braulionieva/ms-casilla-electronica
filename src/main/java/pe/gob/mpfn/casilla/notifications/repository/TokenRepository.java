package pe.gob.mpfn.casilla.notifications.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pe.gob.mpfn.casilla.notifications.model.entity.AuthToken;

import java.util.Optional;

@Repository
public interface TokenRepository extends CrudRepository<AuthToken, Long> {

    Optional<AuthToken> findFirstByUserAndStatusOrderByCreateAtDesc(String dni, String status);

}
