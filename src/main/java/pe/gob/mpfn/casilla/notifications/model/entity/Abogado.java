package pe.gob.mpfn.casilla.notifications.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import pe.gob.mpfn.casilla.notifications.util.enums.ObjetosOracle;

import java.time.LocalDate;

@Table(value = "CFTV_ABOGADO", schema = ObjetosOracle.SISCFE)
@Getter @Setter
public class Abogado {
    @Id
    @Column("ID_V_ABOGADO")
    private String id;

    @Column("ID_N_COLEGIO_ABOGADOS")
    private Long idColegioAbogados;

    @Column("ID_V_PERSONA")
    private String idPersona;

    @Column("NU_V_COLEGIO")
    private String nuColegio;

    @Column("ES_C_ABOGADO")
    private String esAbogado;

    @Column("CO_V_US_CREACION")
    private String coUsCreacion;

    @Column("FE_D_CREACION")
    private LocalDate feDCreacion;

    @Column("CO_V_US_MODIFICACION")
    private String coUsModificacion;

    @Column("FE_D_MODIFICACION")
    private LocalDate feDModificacion;

    @Column("CO_V_US_DESACTIVACION")
    private String coUsDesactivacion;

    @Column("FE_D_DESACTIVACION")
    private LocalDate feDDesactivacion;

    @Column("FL_C_VERIFICADO")
    private String flCVerificado;

    public Abogado() {
        /* Empty */
    }


}
