package pe.gob.mpfn.casilla.notifications.util;

public class EmailUtils {

    private EmailUtils() {
    }

    public static String obfuscateEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex == -1) {
            return email;
        }

        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        if (username.length() <= 2) {
            return email;
        }

        return "%s%s%s".formatted(username.substring(0, 2),
                "****",
                domain);
    }
}
