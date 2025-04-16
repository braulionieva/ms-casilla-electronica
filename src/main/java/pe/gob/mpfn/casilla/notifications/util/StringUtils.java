package pe.gob.mpfn.casilla.notifications.util;

public class StringUtils {

    private StringUtils() {}

    public static String formatearNombre(String nombreCompleto) {
        if (nombreCompleto == null) {
            return "";
        }
        String[] palabras = nombreCompleto.split("\\s+");
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < palabras.length; i++) {
            if ((i + 1) % 2 == 0) {
                resultado.append(palabras[i].charAt(0)).append(". ");
            } else {
                resultado.append(palabras[i]).append(" ");
            }
        }
        return resultado.toString().trim();
    }
}
