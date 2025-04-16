package pe.gob.mpfn.casilla.notifications.util.enums;

public enum TokenStatus {

    REVOKED("0"),
    VALID("1")

    ;

    private final String value;

    TokenStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
