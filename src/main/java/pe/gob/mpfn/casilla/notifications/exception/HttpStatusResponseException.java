package pe.gob.mpfn.casilla.notifications.exception;

import org.springframework.http.HttpStatus;

public class HttpStatusResponseException extends  AbstractHttpStatus{


    public HttpStatusResponseException() {
    }

    public HttpStatusResponseException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatusResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
