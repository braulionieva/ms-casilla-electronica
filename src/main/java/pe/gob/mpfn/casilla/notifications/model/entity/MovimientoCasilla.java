package pe.gob.mpfn.casilla.notifications.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

import static pe.gob.mpfn.casilla.notifications.util.enums.ObjetosOracle.SISCAS;

@Getter
@Setter
@Table(value = "SITV_MOVIMIENTO_CASILLA", schema = SISCAS)
public class MovimientoCasilla {
    @Id
    @Column("ID_V_MOVIMIENTO_CASILLA")
    private String idMovimientoCasilla;

    @Column("ID_V_CASILLA")
    private String idCasilla;

    @Column("ID_N_TIPO_MOVIMIENTO_CASILLA")
    private Integer tipoMovimientoCasilla;

    @Column("FE_D_MOVIMIENTO_CASILLA")
    private LocalDateTime fechaMovimientoCasilla;

    @Column("DE_V_MOVIMIENTO_CASILLA")
    private String descripcionMovimientoCasilla;

    @Column("ES_V_ANTERIOR")
    private String estadoAnterior;

    @Column("ES_V_POSTERIOR")
    private String estadoPosterior;

    @Column("CO_V_US_CREACION")
    private String usuarioCreacion;

    @Column("FE_D_CREACION")
    private LocalDateTime fechaCreacion;

    @Column("CO_V_US_MODIFICACION")
    private String usuarioModificacion;

    @Column("FE_D_MODIFICACION")
    private LocalDateTime fechaModificacion;

    @Column("CO_V_US_DESACTIVACION")
    private String usuarioDesactivacion;

    @Column("FE_D_DESACTIVACION")
    private LocalDateTime fechaDesactivacion;

    @Column("ES_C_MOVIMIENTO_CASILLA")
    private String estadoMovimientoCasilla;
}
