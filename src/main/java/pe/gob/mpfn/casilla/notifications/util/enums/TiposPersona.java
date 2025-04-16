package pe.gob.mpfn.casilla.notifications.util.enums;

public enum TiposPersona {
    NATURAL("1"),
    JURIDICA("2"),
    ABOGADO("8")
    ;


    public String getValue() {
        return value;
    }

    private final String value;

    TiposPersona(String value) {
        this.value = value;
    }

    public static TiposPersona fromValue(String value) {
        for (TiposPersona tipo : TiposPersona.values()) {
            if (tipo.getValue().equals(value)) {
                return tipo;
            }
        }
        return null;
    }
}
