package pe.gob.mpfn.casilla.notifications.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pe.gob.mpfn.casilla.notifications.util.enums.AppParams;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static pe.gob.mpfn.casilla.notifications.util.enums.Messages.INVALID_USER;

public class PwCryptUtils {

    private PwCryptUtils() {
    }

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 16 * 8;
    private static final int IV_LENGTH = 12;
    private static final Logger log = LoggerFactory.getLogger(PwCryptUtils.class);

    public static String decrypt(String encryptedText) {

        try {
            String[] parts = encryptedText.split(",");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Password Inválido: %s".formatted(encryptedText));
            }
            String encryptedText1 = parts[1];

            byte[] keyBytes = AppParams.TOKEN_16_KEY.getValue().getBytes(StandardCharsets.UTF_8);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText1);
            byte[] iv = new byte[IV_LENGTH];
            System.arraycopy(decodedBytes, 0, iv, 0, iv.length);

            byte[] encryptedBytes = new byte[decodedBytes.length - IV_LENGTH];
            System.arraycopy(decodedBytes, IV_LENGTH, encryptedBytes, 0, encryptedBytes.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, spec);

            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (
                InvalidAlgorithmParameterException
                | NoSuchPaddingException
                | IllegalBlockSizeException
                | NoSuchAlgorithmException
                | BadPaddingException
                | InvalidKeyException e) {

            log.error("Error {} {}", encryptedText, e.getMessage());
            throw new UsernameNotFoundException(INVALID_USER.getValue());
        }

    }

}
