package pe.gob.mpfn.casilla.notifications.util.enums;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;


public enum Roles {

    USER,


    ;

    public String value() {
        return "ROLE_%S".formatted(name());
    }

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = new ArrayList<SimpleGrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(value()));
        return authorities;
    }
}
