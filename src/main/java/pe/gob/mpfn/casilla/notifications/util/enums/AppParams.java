package pe.gob.mpfn.casilla.notifications.util.enums;

import lombok.Getter;

@Getter
public enum AppParams {

    EMAIL_GENERATE_CODE_OPTION("_GENERAR_CODIGO"),
    SMS_VALIDATE_CODE_OPTION("_VERIFICAR_CODIGO"),
    EMAIL_GENERATE_CODE_SUCCESSFUL("10"),

    DMY_FORMAT("dd/MM/yyyy"),
    VALIDATION_SUCCESS_CODE("40"),
    SESSION_CODE_KEY("code"),
    USER_CODE_SESSION_KEY("user"),
    SP_SUCCESSFUL_STATUS("0"),
    SP_ERROR_STATUS("1"),
    SP_USER_OR_PASS_INVALID("2"),
    SP_INTERNAL_ERROR("-1"),
    EMAIL_FROM("MP [Notificaciones] <no-reply@mpfn.gob.pe>"),
    SEND_CODE_SUBJECT("Código de cambio de contraseña Casilla Electrónica"),
    ROWS_PER_PAGE(10),
    ACTIVE_ACCOUNT("1"),
    INACTIVE_ACCOUNT("0"),
    CUENTA_ACTIVADA_SUBJECT("Casilla Fiscal Electrónica - Ministerio Público - Fiscalía de la Nación"),
    SOLICITUD_2FA_SUBJECT("Código de verificación para iniciar sesión – Casilla Fiscal Electrónica - Ministerio Público - Fiscalía de la Nación"),
    ACCOUNT_LOCK_SUBJECT("Cuenta bloqueada – Casilla Fiscal Electrónica - Ministerio Público - Fiscalía de la Nación"),
    SOLICITUD_CAMBIO_PASSWORD_SUBJECT("Casilla Fiscal Electrónica - Ministerio Público - Fiscalía de la Nación"),
    SOLICITUD_CODIGO_VERIFICACION_SUBJECT("Código de verificación para recuperar acceso a la Casilla Fiscal Electrónica"),
    EXPIRACION_RECUPERACION_PASSWORD_TOKEN(600),
    CODIGO_GENERADO_SIN_VALIDAR("C"),
    CODIGO_GENERADO_VALIDADO("V"),
    ES_ABOGADO("1"),
    NO_ES_ABOGADO("0"),
    TOKEN_16_KEY("KRx8zM2x7NDbbdcA")
    ;
    private final String value;
    private int intVal = 0;
    AppParams(String value) {
        this.value = value;
    }

    AppParams(int intVal) {
        this.intVal = intVal;
        value = "";
    }
}
