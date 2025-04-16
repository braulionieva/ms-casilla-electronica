package pe.gob.mpfn.casilla.notifications.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pe.gob.mpfn.casilla.notifications.model.projection.AbogadoProjection;

@ToString
@Getter @Setter
public class AccountRecord {
    @NotBlank
    private String correo;
    @NotBlank
    private String numDoc;
    @NotBlank
    private String nombre;
    @NotBlank
    private String apePat;
    @NotBlank
    private String apeMat;
    private String firstLogin;
    private String tipoUsr;
    @NotBlank
    private String cellphone;
    private String cuentaId;
    private String abogado;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AbogadoProjection otros;
    private String idPersona;
    private String usuario;
    private String defensorPublico;
    private String tiene2fa;
    private String cuentaBloqueada;

    public AccountRecord(String correo, String numDoc, String nombre, String apePat, String apeMat, String cellphone, String cuentaId) {
        this.correo = correo;
        this.numDoc = numDoc;
        this.nombre = nombre;
        this.apePat = apePat;
        this.apeMat = apeMat;
        this.cellphone = cellphone;
        this.cuentaId = cuentaId;
    }

    public AccountRecord(String correo, String numDoc, String nombre, String apePat, String apeMat, String flagActCta, String tipoUsr, String cellphone) {
        this(correo, numDoc, nombre, apePat, apeMat, cellphone, null);
        this.firstLogin = flagActCta;
        this.tipoUsr = tipoUsr;
    }
}

record AdicionalesAbogado(String idColegio, String numeroColegiatura){}
