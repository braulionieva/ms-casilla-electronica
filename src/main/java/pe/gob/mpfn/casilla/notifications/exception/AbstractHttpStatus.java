package pe.gob.mpfn.casilla.notifications.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class AbstractHttpStatus extends RuntimeException {


    protected HttpStatus httpStatus;

    protected AbstractHttpStatus(String message) {
        super(message);
    }

    protected AbstractHttpStatus(String message, Throwable cause) {
        super(message, cause);
    }

    protected AbstractHttpStatus() {
    }
}
