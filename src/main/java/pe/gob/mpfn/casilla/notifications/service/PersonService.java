package pe.gob.mpfn.casilla.notifications.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.mpfn.casilla.notifications.model.dto.AccountRecord;
import pe.gob.mpfn.casilla.notifications.model.entity.Person;
import pe.gob.mpfn.casilla.notifications.repository.PersonRepository;

@Service
@RequiredArgsConstructor
public class PersonService {
    private static final Logger log = LoggerFactory.getLogger(PersonService.class);
    private final PersonRepository personRepository;

    @Transactional
    public Person update(AccountRecord accountDto) {

        var person = personRepository.findByNumDoc(accountDto.getNumDoc()).orElseThrow(() ->{
        log.info("Usuario no encontrado: %s".formatted(accountDto.getNumDoc()));
        return new UsernameNotFoundException("Usuario no encontrado");
        });
        person.setNumCel(accountDto.getCellphone());
        person.setEmail(accountDto.getCorreo());

        return personRepository.save(person);
    }


}
