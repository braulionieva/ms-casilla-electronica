package pe.gob.mpfn.casilla.notifications.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class CustomNotificationRepositoryImplTest {

    @Autowired
    NotificationRepository notificationRepository;

    @Test
    void archivarNotificaciones() {

        var res = notificationRepository.archivarNotificaciones("z", List.of("23", "32"));
        Assertions.assertThat(res).isTrue();

    }
}