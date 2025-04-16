package pe.gob.mpfn.casilla.notifications.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pe.gob.mpfn.casilla.notifications.model.entity.Abogado;
import pe.gob.mpfn.casilla.notifications.model.projection.AbogadoProjection;

import java.util.Optional;

@Repository
public interface AbogadoRepository extends CrudRepository<Abogado, String> {

    Optional<AbogadoProjection> findFirstByIdPersonaAndEsAbogado(String idVPersona, String estadoAbogado);
}