package pe.gob.mpfn.casilla.notifications.model.dto;

import org.springframework.http.HttpStatus;

public record ValidateCodeResponse(boolean success, int code, String message) {

    public ValidateCodeResponse(boolean success, String message) {
        this(success, 0, message);
    }

    public ValidateCodeResponse() {
        this(true, HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase());
    }
}
