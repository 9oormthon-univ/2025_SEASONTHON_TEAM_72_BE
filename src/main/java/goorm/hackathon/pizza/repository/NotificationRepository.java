package goorm.hackathon.pizza.repository;

import goorm.hackathon.pizza.entity.Notification;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipient_UserId(Long userId, Sort sort);

    List<Notification> findByRecipient_UserIdAndIsReadFalse(Long userId, Sort sort);

    long countByRecipient_UserIdAndIsReadFalse(Long userId);
}
