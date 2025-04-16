package pe.gob.mpfn.casilla.notifications.model.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import static pe.gob.mpfn.casilla.notifications.util.enums.ObjetosOracle.SISCAS;

@Table(name = "CEL_PERSONA", schema = SISCAS)
@Data
public class Person {
    @Id
    @Column("PERSONA_ID")
    private Long id;
    @Column("TIPO_DOC_ID")
    private String tipoDocId;
    @Column("NUM_DOC")
    private String numDoc;
    @Column("TIPO_PERS")
    private String tipoPers;
    @Column("CORREO")
    private String email;
    @Column("NUM_CEL")
    private String numCel;


}
