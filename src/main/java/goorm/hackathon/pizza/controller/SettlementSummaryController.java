// src/main/java/goorm/hackathon/pizza/controller/SettlementSummaryController.java
package goorm.hackathon.pizza.controller;

import goorm.hackathon.pizza.dto.response.settlement.SettlementSummaryDto;
import goorm.hackathon.pizza.entity.User;
import goorm.hackathon.pizza.service.SettlementSummaryService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/settlements")
public class SettlementSummaryController {

    private final SettlementSummaryService settlementSummaryService;

    public SettlementSummaryController(SettlementSummaryService settlementSummaryService) {
        this.settlementSummaryService = settlementSummaryService;
    }

    @GetMapping("/{id}/summary")
    public SettlementSummaryDto getSummary(
            @PathVariable("id") Long settlementId,
            @AuthenticationPrincipal(expression = "firebaseUid") String uid
    ) {
        return settlementSummaryService.getSummary(settlementId, uid); // uid로 권한체크
    }

}
