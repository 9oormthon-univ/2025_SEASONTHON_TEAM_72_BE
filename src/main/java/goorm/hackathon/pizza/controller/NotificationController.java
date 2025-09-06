package goorm.hackathon.pizza.controller;

import goorm.hackathon.pizza.dto.response.notification.NotificationResponse;
import goorm.hackathon.pizza.service.NotificationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationQueryService notificationQueryService;
     //전체 알림(최신순) –배열로 반환
     //GET /api/v1/notifications?userId=101

    @GetMapping
    public List<NotificationResponse> getMyNotifications(@RequestParam("userId") Long userId) {
        return notificationQueryService.findMyNotifications(userId);
    }
}
