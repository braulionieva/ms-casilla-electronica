package pe.gob.mpfn.casilla.notifications.util.enums;

import lombok.Getter;

@Getter
public enum NotificationStatus {

    RECEIVED("0"),
    READ("1")
    ;
    private final String value;
    NotificationStatus(String value) {
        this.value = value;
    }
}
