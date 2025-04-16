package pe.gob.mpfn.casilla.notifications.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pe.gob.mpfn.casilla.notifications.model.dto.ValidateCodeResponse;

import static org.springframework.http.HttpStatus.*;
import static pe.gob.mpfn.casilla.notifications.util.enums.Messages.BAD_CREDENTIALS;

@RestControllerAdvice
public class AppExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(AppExceptionHandler.class);

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ValidateCodeResponse> badCredentialsHandler(BadCredentialsException exception) {
        return ResponseEntity.status(UNAUTHORIZED).body(new ValidateCodeResponse(false, UNAUTHORIZED.value(), BAD_CREDENTIALS.getValue()));
    }

    @ExceptionHandler({NotificationNotFound.class, AffiliateNotFoundException.class, InactiveAccountException.class})
    public ResponseEntity<ValidateCodeResponse> notFoundHandler(RuntimeException exception) {
        return ResponseEntity.status(NOT_FOUND).body(new ValidateCodeResponse(false, NOT_FOUND.value(), exception.getMessage()));
    }

    @ExceptionHandler()
    public ResponseEntity<ValidateCodeResponse> serverErrorHandler(Exception e) {
        log.info(e.getMessage(), e);
        return ResponseEntity.internalServerError().body(new ValidateCodeResponse(false, INTERNAL_SERVER_ERROR.value(), "Ha ocurrido un error, inténtelo más tarde"));

    }

    @ExceptionHandler({
            HttpStatusResponseException.class
    })
    public ResponseEntity<ValidateCodeResponse> handleExceptions(AbstractHttpStatus exception) {

        return ResponseEntity.status(exception.getHttpStatus()).body(
                new ValidateCodeResponse(false,
                        exception.getHttpStatus().value(),
                        exception.getMessage()
                )
        );
    }


}
