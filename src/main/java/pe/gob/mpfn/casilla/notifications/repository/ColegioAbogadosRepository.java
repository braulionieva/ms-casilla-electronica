package pe.gob.mpfn.casilla.notifications.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pe.gob.mpfn.casilla.notifications.model.entity.ColegioAbogados;
import pe.gob.mpfn.casilla.notifications.model.projection.ColegioAbogadosProjection;

import java.util.List;

@Repository
public interface ColegioAbogadosRepository extends CrudRepository<ColegioAbogados, Long> {

    List<ColegioAbogadosProjection> findByEstadoOrderByNombreAsc(String estado) ;
}
