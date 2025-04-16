package pe.gob.mpfn.casilla.notifications.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import static pe.gob.mpfn.casilla.notifications.util.enums.ObjetosOracle.SISCAS;
import static pe.gob.mpfn.casilla.notifications.util.enums.ObjetosOracle.SISCASBF;

@Table(name = "SITV_CASILLA", schema = SISCAS)
@Getter
@Setter
public class Account {
    @Id
    @Column("ID_V_CASILLA")
    private String id;
    @Column("CO_V_USUARIO")
    private String usuario;
    @Column("PW_V_USUARIO")
    private String clave;
    @Column("ES_C_CASILLA")
    private String estado;
    @Column("ID_V_PERSONA")
    private String idPerona;
    @Column("DE_V_CORREO")
    private String correo;
    @Column("ID_N_TIPO_PERSONA")
    private int tipoCasilla;
    @Column("FL_C_PRIMER_LOGIN")
    private String firstLogin;
}
