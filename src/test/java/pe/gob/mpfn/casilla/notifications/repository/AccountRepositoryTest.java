package pe.gob.mpfn.casilla.notifications.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
class AccountRepositoryTest {

    @Autowired
    AccountRepository accountRepository;

    @Test
    void findFirstByUsuario() {

        var account = accountRepository.findFirstByUsuario("47850132");
        assertThat(account).isNotEmpty();

    }

    @Test
    void encodedPw() {
        var ss = accountRepository.encodedPw("4785450132");
        System.out.println(ss);
        assertThat(ss).isNotEmpty();
    }

    @Test
    void usersWithInitialPassword() {

        var result = accountRepository.usersWithInitialPassword();
        System.out.println(result);
        assertThat(result).isNotNull();

    }
}