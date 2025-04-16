package pe.gob.mpfn.casilla.notifications.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pe.gob.mpfn.casilla.notifications.model.entity.MovimientoCasilla;

@Repository
public interface MovimientoCasillaRepository extends CrudRepository<MovimientoCasilla, String> {
}
