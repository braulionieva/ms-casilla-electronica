package pe.gob.mpfn.casilla.notifications.model.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static pe.gob.mpfn.casilla.notifications.util.enums.ObjetosOracle.SISCAS;

@Data
@Table(value = "NOTIFICATION", schema = SISCAS)
public class Notification {

    @Id
    @Column("ID")
    private String id;
    @Column("AFFILIATE_ID")
    private Long affiliateId;
    @Column("AFFILIATE_NAME")
    private String affiliateName;
    @Column("SUBJECT")
    private String subject;
    @Column("CASE_NUMBER")
    private String caseNumber;
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
    @Column("TAG")
    private String tag;
    @Column("FOLDER")
    private String folder;
    @Transient
    private List<NotificationAttachment> notificationAttachments = new ArrayList<>();

}