package pe.gob.mpfn.casilla.notifications.security;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DocumentosGeneralesServiceTest {

    @Autowired
    private DocumentosGeneralesService documentosGeneralesService;

    @Test
    void download() {

        var result = documentosGeneralesService.download("1911B5DD33238248E0650250569D508A", "17CB1ED591BA0CFFE0650250569D508A", "1911E55FE57E8132E0650250569D508A");
        System.out.println(result);
        Assertions.assertThat(result).isNotNull();

    }
}