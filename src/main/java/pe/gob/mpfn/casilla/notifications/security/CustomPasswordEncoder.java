package pe.gob.mpfn.casilla.notifications.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.gob.mpfn.casilla.notifications.repository.AccountRepository;

@RequiredArgsConstructor
public class CustomPasswordEncoder implements PasswordEncoder {

    private final AccountRepository accountRepository;

    @Override
    public String encode(CharSequence rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("rawPassword cannot be null");
        }
        return accountRepository.encodedPw(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("rawPassword cannot be null");
        }
        if (encodedPassword == null || encodedPassword.isEmpty()) {
            return false;
        }
        return encodedPassword.equals(encode(rawPassword));
    }
}
