package pe.gob.mpfn.casilla.notifications.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pe.gob.mpfn.casilla.notifications.security.AuthenticationRequest;

@SpringBootTest
class MovimientoCasillaServiceTest {

    @Autowired
    private AffiliateService affiliateService;
    @Autowired
    private MovimientoCasillaService movimientoCasillaService;


    @Test
    void createAndSaveFirstLogin() {

        var userDetail = affiliateService.getAccountData(new AuthenticationRequest(
                "43465646",
                "12345678*aA",
                ""
        ));

        Assertions.assertThat(userDetail).isNotNull();

        movimientoCasillaService.createAndSaveFirstLogin(userDetail);


    }
}