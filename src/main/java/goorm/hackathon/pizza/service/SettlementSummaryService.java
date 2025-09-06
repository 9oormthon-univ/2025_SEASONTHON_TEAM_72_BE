// src/main/java/goorm/hackathon/pizza/service/SettlementSummaryService.java
package goorm.hackathon.pizza.service;

import goorm.hackathon.pizza.dto.response.settlement.SettlementSummaryDto;
import goorm.hackathon.pizza.dto.response.settlement.SummaryItemDto;
import goorm.hackathon.pizza.dto.response.settlement.SummaryUserDto;
import goorm.hackathon.pizza.entity.Settlement;
import goorm.hackathon.pizza.repository.AllocationRepository;
import goorm.hackathon.pizza.repository.SettlementRepository;
import goorm.hackathon.pizza.repository.rows.UserItemRow;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SettlementSummaryService {

    private final AllocationRepository allocationRepository;
    private final SettlementRepository settlementRepository;

    public SettlementSummaryService(AllocationRepository allocationRepository,
                                    SettlementRepository settlementRepository) {
        this.allocationRepository = allocationRepository;
        this.settlementRepository = settlementRepository;
    }

    /**
     * @param settlementId 정산방 ID
     * @param uid          호출자 식별(Firebase UID) - 권한 체크 용도로 필요시 사용
     */
    public SettlementSummaryDto getSummary(Long settlementId, String uid) {
        // 0) 제목은 DB에서 조회 (UID를 제목으로 쓰는 버그 방지)
        String title = settlementRepository.findById(settlementId)
                .map(Settlement::getTitle)
                .orElse("정산");

        // 1) 사용자-아이템 단위행 조회
        List<UserItemRow> rows = allocationRepository.findUserItemRows(settlementId);

        // 2) 사용자 닉네임 기준 그룹핑
        Map<String, List<UserItemRow>> byUser = rows.stream()
                .collect(Collectors.groupingBy(
                        UserItemRow::getUserNickname,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        // 3) 변환
        List<SummaryUserDto> users = new ArrayList<>();

        for (Map.Entry<String, List<UserItemRow>> e : byUser.entrySet()) {
            String nickname = e.getKey();
            List<UserItemRow> list = e.getValue();

            // 동일 유저 묶음 → 첫 행에서 userId/paid 꺼내기
            Long userId = list.stream().findFirst().map(UserItemRow::getUserId).orElse(null);
            boolean paid = list.stream().findFirst().map(UserItemRow::isPaid).orElse(false);

            // 아이템 리스트
            List<SummaryItemDto> items = list.stream().map(r -> {
                BigDecimal unit = Optional.ofNullable(r.getUnitPrice()).orElse(BigDecimal.ZERO);
                int unitPrice = unit.setScale(0, RoundingMode.HALF_UP).intValue();

                SummaryItemDto it = new SummaryItemDto();
                it.setName(r.getItemName());
                it.setQuantity(r.getQuantity()); // int
                it.setPrice(unitPrice);          // int
                return it;
            }).toList();

            SummaryUserDto u = new SummaryUserDto();
            u.setUserId(userId);     // ← 여기! userId 세팅
            u.setUser(nickname);
            u.setPaid(paid);
            u.setItems(items);

            users.add(u);
        }

        SettlementSummaryDto dto = new SettlementSummaryDto();
        dto.setTitle(title);
        dto.setSettlementId(settlementId);
        dto.setUsers(users);
        return dto;
    }
}
