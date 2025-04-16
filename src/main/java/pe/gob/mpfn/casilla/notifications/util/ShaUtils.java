package pe.gob.mpfn.casilla.notifications.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ShaUtils {

    private ShaUtils() {
    }

    private static final Logger log = LoggerFactory.getLogger(ShaUtils.class);

    /**
     * @deprecated use UUID
     * @param dateString
     * @return
     */
    @Deprecated(forRemoval = true)
    public static  String toSha256(String dateString) {
        String sha256Hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hashBytes = digest.digest(dateString.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            sha256Hash = hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            log.info("Error", e);
        }

        return sha256Hash;
    }

    public static String sanitizeInput(String input) {
        if (input != null) {
            return input.replaceAll("['\";]", "");
        }
        return null;

    }

}
