package pe.gob.mpfn.casilla.notifications.model.dto;

public record UpdateEmailRequest(String code, String email, String oldEmail) {}
