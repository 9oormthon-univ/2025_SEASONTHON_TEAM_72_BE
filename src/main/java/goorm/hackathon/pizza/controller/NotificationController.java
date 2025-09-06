package goorm.hackathon.pizza.controller;

import goorm.hackathon.pizza.dto.response.notification.NotificationResponse;
import goorm.hackathon.pizza.service.NotificationQueryService;
import goorm.hackathon.pizza.util.AuthUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationQueryService service;

    public NotificationController(NotificationQueryService service) {
        this.service = service;
    }

    @GetMapping
    public List<NotificationResponse> list() {
        Long userId = AuthUtils.currentUserIdOrThrow();
        return service.getAll(userId);
    }
}
