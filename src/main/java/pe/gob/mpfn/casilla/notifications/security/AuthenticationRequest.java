package pe.gob.mpfn.casilla.notifications.security;

public record AuthenticationRequest(String usuario, String password, String token, String ip, String dispositivo, String code) {

    public AuthenticationRequest(String usuario) {
        this(usuario, "", "", "", "", "");
    }

}