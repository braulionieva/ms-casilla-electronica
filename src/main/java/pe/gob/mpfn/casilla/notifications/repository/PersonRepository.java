package pe.gob.mpfn.casilla.notifications.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pe.gob.mpfn.casilla.notifications.model.entity.Person;

import java.util.Optional;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {

    Optional<Person> findByNumDoc(String numDoc);

}
