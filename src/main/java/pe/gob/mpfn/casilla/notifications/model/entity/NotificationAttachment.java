package pe.gob.mpfn.casilla.notifications.model.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import static pe.gob.mpfn.casilla.notifications.util.enums.ObjetosOracle.SISCAS;

@Data
@Table(value = "NOTIFICATION_ATTACHMENT", schema = SISCAS)
public class NotificationAttachment {

    @Id
    @Column("ID")
    private Long id;

    @Column("NOTIFICATION_ID")
    private Long notificationId;

    @Column("DOCUMENT_TYPE")
    private String documentType;

    @Column("FILENAME")
    private String fileName;

    @Column("URL")
    private String url;

}

