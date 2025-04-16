package pe.gob.mpfn.casilla.notifications.util.enums;

import lombok.Getter;

@Getter
public enum Messages {

    AFFILIATE_NOT_FOUND("Este usuario no se encuentra registrado."),
    INVALID_CODE("Código inválido."),
    INVALID_USER("Usuario inválido."),
    SOLICITUD_INVALIDA("Solicitud inválida"),
    GENERIC_500_ERROR("Error al procesar la información, inténtelo más tarde."),
    BAD_CREDENTIALS("Usuario o contraseña inválidos."),
    BAD_CREDENTIALS_ATTEMPTS("Usuario y/o contraseña incorrecta|El usuario y/o contraseña ingresada es incorrecta. Tener en cuenta que le quedan <strong>[ATTEMPTS] intento(s)</strong>, luego se bloqueará su cuenta."),
    WRONG_VERIFICATION_CODE_ATTEMPTS("Código de verificación incorrecto|El código de verificación ingresado es incorrecto. Tener en cuenta que le quedan <strong>[ATTEMPTS] intento(s)</strong>, luego se bloqueará su cuenta."),
    INACTIVE_ACCOUNT_MESSAGE("El usuario con el que está intentando ingresar está inactivo.")
        ;

    private final String value;

    Messages(String value) {
        this.value = value;
    }
}
