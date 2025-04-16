package pe.gob.mpfn.casilla.notifications.util.enums;

import java.util.Set;

public enum LoginType {
    ABOGADO("Xq7f"),
    DEFENSOR_PUBLICO("Gy2P"),
    PERSONA_NATURAL("Tn8L");

    private final String value;

    LoginType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    private static final Set<String> validLoginTypes = Set.of(
            LoginType.ABOGADO.getValue(),
            LoginType.DEFENSOR_PUBLICO.getValue(),
            LoginType.PERSONA_NATURAL.getValue()
    );

    public static boolean isValidLoginType(String value) {
        return validLoginTypes.contains(value);
    }
}
