package pe.gob.mpfn.casilla.notifications.model.dto;

import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SearchNotificationDto {
    private String idCasilla;
    private String idPersona;
    private String sessionType;
    private String tramite;
    private String caseNumber;
    private String proceduralAct;
    private String notificationNumber;
    private Date createdAtStart;
    private Date createdAtEnd;
    private int pageSize = 10;
    private int pageNumber = 1;
    private String tag;
    private String folder;

}
