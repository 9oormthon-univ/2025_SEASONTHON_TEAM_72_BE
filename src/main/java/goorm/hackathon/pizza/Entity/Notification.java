package goorm.hackathon.pizza.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    // 공지 수신자 (Notification N : 1 User)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(name = "notification_type")
    private String type; // 예: "정산요청", "입금확인"

    @Column(nullable = false)
    private String message;

    @Column(name = "link_type")
    private String linkType; // 예: "settlement", "notice"

    @Column(name = "link_id")
    private Long linkId; // 연결될 엔티티의 ID (예: settlement_id)

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
