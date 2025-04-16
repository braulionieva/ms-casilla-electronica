package pe.gob.mpfn.casilla.notifications.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import pe.gob.mpfn.casilla.notifications.model.entity.Notification;
import pe.gob.mpfn.casilla.notifications.model.entity.SitvNotificacion;

import java.util.List;

@Repository
public interface NotificationRepository extends CrudRepository<SitvNotificacion, String>, ListPagingAndSortingRepository<SitvNotificacion, String>, CustomNotificationRepository {

    List<Notification> findByAffiliateIdAndFolder(String dni, String id);

    List<Notification> findByAffiliateIdAndTag(String dni, String id);

}
