package goorm.hackathon.pizza.dto.response.notification;

import goorm.hackathon.pizza.entity.Enum.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        NotificationType type,
        String message,
        boolean read,
        String status,
        String role,
        LocalDateTime createdAt
) {}
