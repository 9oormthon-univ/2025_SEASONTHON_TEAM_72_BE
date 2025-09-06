package goorm.hackathon.pizza.entity;

import goorm.hackathon.pizza.entity.Enum.LinkType;
import goorm.hackathon.pizza.entity.Enum.NotificationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false, length = 200)
    private String message;

    @Enumerated(EnumType.STRING)
    private LinkType linkType;

    private Long linkId;

    private boolean isRead = false;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder
    public Notification(User recipient,
                        NotificationType type,
                        String message,
                        LinkType linkType,
                        Long linkId,
                        boolean isRead) {
        this.recipient = recipient;
        this.type = type;
        this.message = message;
        this.linkType = linkType;
        this.linkId = linkId;
        this.isRead = isRead;
    }
}
