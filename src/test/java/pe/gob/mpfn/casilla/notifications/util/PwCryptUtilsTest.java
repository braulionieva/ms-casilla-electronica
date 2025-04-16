package pe.gob.mpfn.casilla.notifications.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;

class PwCryptUtilsTest {

    @Test
    void decryptPassword() {
        System.out.println(new Date());
        var result = PwCryptUtils.decrypt("PWD_HEX#:7:1742404175067,X+lTRIfbS5C93PfRR6rayp3um2CfDZypFxohXPaoXYHFoK2WCQ==");

        Assertions.assertThat(result).isNotNull();

        System.out.println(result);

    }
}