package goorm.hackathon.pizza.repository;

import goorm.hackathon.pizza.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    //전체 알림을 최신순(createdAt DESC, id DESC 보조)으로 반환
    List<Notification> findByRecipient_UserIdOrderByCreatedAtDescIdDesc(Long userId);

    // 안 읽은
    long countByRecipient_UserIdAndIsReadFalse(Long userId);
}
