package pe.gob.mpfn.casilla.notifications.model.projection;

public interface AccountProjection {
    String getUsuario();
    String getCorreo();
    String getClave();
    String getEstado();
    String getId();
    String getIdPerona();
    int getTipoCasilla();

}
