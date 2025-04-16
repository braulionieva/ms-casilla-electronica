package pe.gob.mpfn.casilla.notifications.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

import static pe.gob.mpfn.casilla.notifications.util.enums.ObjetosOracle.SISCAS;

@Getter @Setter
@Table(name = "SITV_NOTIFICACION", schema = SISCAS)
public class SitvNotificacion {

    @Id
    @Column("ID_V_NOTIFICACION")
    private String id;

    @Column("AFFILIATE_ID")
    private Long affiliateId;

    @Column("AFFILIATE_NAME")
    private String affiliateName;

    @Column("CASE_NUMBER")
    private String caseNumber;

    @Column("SUBJECT")
    private String subject;

    @Column("NOTIFICATION_NUMBER")
    private String notificationNumber;

    @Column("PROCEDURAL_ACT")
    private String proceduralAct;

    @Column("STATUS")
    private String status;

    @Column("TYPE")
    private String type;

    @Column("CREATED_AT")
    private Timestamp createdAt;

    @Column("READ_AT")
    private Timestamp readAt;

    @Column("FOLDER")
    private String folder;

    @Column("TAG")
    private String tag;

}
