// src/main/java/goorm/hackathon/pizza/dto/response/notification/NotificationSimpleResponse.java
package goorm.hackathon.pizza.dto.response.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import goorm.hackathon.pizza.entity.Enum.NotificationType;
import goorm.hackathon.pizza.entity.Notification;

import java.time.LocalDateTime;

public record NotificationSimpleResponse(
        @JsonProperty("notification_id") Long notificationId,
        @JsonProperty("settlement_id") Long settlementId,
        @JsonProperty("recipient_user_id") Long recipientUserId,
        String message,
        NotificationType type,
        @JsonProperty("created_at") LocalDateTime createdAt
) {
    public static NotificationSimpleResponse of(Notification n) {
        return new NotificationSimpleResponse(
                n.getId(),
                n.getLinkId(),
                n.getRecipient().getUserId(),
                n.getMessage(),
                n.getType(),
                n.getCreatedAt()
        );
    }
}
