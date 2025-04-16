package pe.gob.mpfn.casilla.notifications.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationAttachmentDto {
    private Long notificationId;
    private String documentType;
    private String filename;
    private String url;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}