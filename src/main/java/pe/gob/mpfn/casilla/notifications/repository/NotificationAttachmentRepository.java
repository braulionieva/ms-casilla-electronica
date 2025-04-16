package pe.gob.mpfn.casilla.notifications.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pe.gob.mpfn.casilla.notifications.model.entity.NotificationAttachment;

import java.util.List;

@Repository
public interface NotificationAttachmentRepository extends CrudRepository<NotificationAttachment, String> {
    List<NotificationAttachment> findByNotificationId(String id);
}
