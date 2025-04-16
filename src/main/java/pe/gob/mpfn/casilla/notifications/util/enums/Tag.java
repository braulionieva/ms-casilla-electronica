package pe.gob.mpfn.casilla.notifications.util.enums;

import lombok.Getter;

@Getter
public enum Tag {
    CITACION("Citaciones", "C"),
    NOTIFICACION("Notificaciones", "N")
    ;

    private final String value;
    private final String key;

    Tag(String value, String key) {
        this.value = value;
        this.key = key;
    }
}
