package pe.gob.mpfn.casilla.notifications.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

import static pe.gob.mpfn.casilla.notifications.util.enums.ObjetosOracle.SISCAS;

@Table(name = "SITT_CODIGO", schema = SISCAS)
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class SittCodigo {

    @Id
    @Column("ID_V_CODIGO_SMS")
    private String id;

    @Column("NU_V_CELULAR")
    private String celular;

    @Column("CO_V_AUTOGENERADO")
    private String codigoGenerado;

    @Column("IP_V_ADDRESS")
    private String ipAddress;

    @Column("FL_C_INDICADOR")
    private String flCIndicador;

    @Column("FE_D_CREACION")
    private LocalDate feDCreacion;

    @Column("FE_D_MODIFICACION")
    private LocalDate feDModificacion;

    @Column("CO_V_USUARIO")
    private String coUsuario;
}
