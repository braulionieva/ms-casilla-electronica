package pe.gob.mpfn.casilla.notifications.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SecurityResponseDto {

    private String code;
    private String message;
    private int verificationResponse;
    private String verificationCode;
    private String expiration;
    private int remainingAttempts;

}