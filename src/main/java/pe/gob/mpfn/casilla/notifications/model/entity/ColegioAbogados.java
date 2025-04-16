package pe.gob.mpfn.casilla.notifications.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

import static pe.gob.mpfn.casilla.notifications.util.enums.ObjetosOracle.SISMAEST;

@Getter @Setter
@Table(value = "CFTM_COLEGIO_ABOGADOS", schema = SISMAEST)
public class ColegioAbogados {

    @Id
    @Column("ID_N_COLEGIO_ABOGADOS")
    private Long id;

    @Column("NO_V_COLEGIO_ABOGADOS")
    private String nombre;

    @Column("ES_C_COLEGIO_ABOGADOS")
    private String estado;

    @Column("NO_V_SEDE")
    private String sede;

    @Column("DI_V_RESIDENCIA")
    private String direccion;

    @Column("CO_V_US_CREACION")
    private String usuarioCreacion;

    @Column("FE_D_CREACION")
    private Date fechaCreacion;

    @Column("CO_V_US_MODIFICACION")
    private String usuarioModificacion;

    @Column("FE_D_MODIFICACION")
    private Date fechaModificacion;

    @Column("CO_V_US_DESACTIVACION")
    private String usuarioDesactivacion;

    @Column("FE_D_DESACTIVACION")
    private Date fechaDesactivacion;

    // Getters and Setters

}
