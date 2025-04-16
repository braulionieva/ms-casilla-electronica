package pe.gob.mpfn.casilla.notifications.model.dto;

import java.util.List;

public record SearchNotificationResponseDto(List<?> notifications, int total, int size) {
}
