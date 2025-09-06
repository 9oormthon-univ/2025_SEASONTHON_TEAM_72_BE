package goorm.hackathon.pizza.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notification_settings")
public class NotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setting_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private boolean notifyOnInProgress = true;
    private boolean notifyOnUnpaid = true;
    private boolean notifyOnUnpaidMembers = true;

    private int reminderCycle = 3;

    @Builder
    public NotificationSetting(User user, boolean notifyOnInProgress, boolean notifyOnUnpaid, boolean notifyOnUnpaidMembers, int reminderCycle) {
        this.user = user;
        this.notifyOnInProgress = notifyOnInProgress;
        this.notifyOnUnpaid = notifyOnUnpaid;
        this.notifyOnUnpaidMembers = notifyOnUnpaidMembers;
        this.reminderCycle = reminderCycle;
    }
}