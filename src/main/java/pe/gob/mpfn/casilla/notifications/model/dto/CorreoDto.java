package pe.gob.mpfn.casilla.notifications.model.dto;

public record CorreoDto(

    String asunto,
    String destinatario,
    String conCopia,
    String cuerpo,
    String adjunto,
    String nombreAdjunto){

    public CorreoDto(String asunto, String destinatario, String cuerpo) {
        this(asunto, destinatario, null, cuerpo, null, null);
    }

    public CorreoDto(String asunto, String destinatario, String cuerpo, String adjunto) {
        this(asunto, destinatario, null, cuerpo, adjunto, "TÉRMINOS Y CONDICIONES -CASILLA ELÉCTRONICA.PDF");
    }
}

