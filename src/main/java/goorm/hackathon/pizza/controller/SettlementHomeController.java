package goorm.hackathon.pizza.controller;

import goorm.hackathon.pizza.dto.response.settlement.SettlementListItemResponse;
import goorm.hackathon.pizza.service.SettlementQueryService;
import goorm.hackathon.pizza.util.AuthUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/settlements")
public class SettlementHomeController {

    private final SettlementQueryService service;

    public SettlementHomeController(SettlementQueryService service) {
        this.service = service;
    }

    // 진행중 목록: 배열만 반환
    @GetMapping("/ongoing")
    public List<SettlementListItemResponse> ongoing() {
        Long userId = AuthUtils.currentUserIdOrThrow();
        return service.getOngoing(userId);
    }

    // 종료 목록: 배열만 반환
    @GetMapping("/completed")
    public List<SettlementListItemResponse> completed() {
        Long userId = AuthUtils.currentUserIdOrThrow();
        return service.getCompleted(userId);
    }
}
