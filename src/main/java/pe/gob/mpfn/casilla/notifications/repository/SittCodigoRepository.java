package pe.gob.mpfn.casilla.notifications.repository;

import org.springframework.data.repository.CrudRepository;
import pe.gob.mpfn.casilla.notifications.model.entity.SittCodigo;

import java.util.Optional;

public interface SittCodigoRepository extends CrudRepository<SittCodigo, String> {


    Optional<SittCodigo> findFirstByCodigoGeneradoAndFlCIndicador(String token, String indicador);
}
