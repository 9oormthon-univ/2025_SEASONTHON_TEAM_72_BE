// ReminderResponse.java
package goorm.hackathon.pizza.dto.response.notification;

import goorm.hackathon.pizza.entity.Enum.NotificationType;
import goorm.hackathon.pizza.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ReminderResponse {
    private Long notificationId;
    private Long settlementId;
    private Long recipientUserId;
    private String message;
    private NotificationType type;
    private LocalDateTime createdAt;

    public static ReminderResponse of(Notification n) {
        return ReminderResponse.builder()
                .notificationId(n.getId())
                .settlementId(n.getLinkId())
                .recipientUserId(n.getRecipient().getUserId())
                .message(n.getMessage())
                .type(n.getType())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
