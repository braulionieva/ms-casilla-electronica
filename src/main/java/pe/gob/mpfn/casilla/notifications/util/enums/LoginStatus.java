package pe.gob.mpfn.casilla.notifications.util.enums;

public enum LoginStatus {

    REGISTERED("0"),
    FIRST_PASSWORD("1"),
    FIRST_LOGIN("2")
    ;

    private final String value;

    LoginStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
