package pe.gob.mpfn.casilla.notifications.util.enums;

import lombok.Getter;

@Getter
public enum Folder {
    RECIBIDOS("Recibidos","1"),
    DESTACADO("Destacados", "3"),
    IMPORTANTE("Importantes", "4"),
    LEIDO("Leídos","2"),
    ARCHIVADOS("Archivados","5" )

    ;

    private final String value;
    private final String key ;

    Folder(String value, String key) {
        this.value = value;
        this.key = key;
    }
}
