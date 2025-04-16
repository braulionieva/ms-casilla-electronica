package pe.gob.mpfn.casilla.notifications.model.dto.rest;

public record ReCaptchaResponse(
        boolean success,
        String challenge_ts, // timestamp in ISO format (yyyy-MM-dd'T'HH:mm:ssZZ)
        String hostname,
        String[] error_codes // optional array of error codes
) {}

