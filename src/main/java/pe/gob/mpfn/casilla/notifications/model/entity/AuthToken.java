package pe.gob.mpfn.casilla.notifications.model.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

import static pe.gob.mpfn.casilla.notifications.util.enums.ObjetosOracle.SISCAS;

@Table(name = "SITT_AUTENTICACION_USUARIO", schema = SISCAS)
@Data
@Builder
public class AuthToken {


    @Id
    @Column("ID_V_AUTENTICACION_USUARIO")
    private String id;
    @Column("CO_V_USUARIO")
    private String user;
    @Column("DE_V_SESION_TOKEN")
    private String token;
    @Column("ES_C_AUTENTICACION_USUARIO")
    /* revoked or valid */
    private String status;
    @Column("FE_D_CREACION")
    private Date createAt;
    @Column("IN_C_BLOQUEADO")
    @Builder.Default
    private String bloqueado = "0";
    @Column("FE_D_MODIFICACION")
    @Builder.Default
    private Date modifiedAt = new Date();

}
