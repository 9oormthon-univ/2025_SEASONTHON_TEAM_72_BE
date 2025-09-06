package goorm.hackathon.pizza.service;

import goorm.hackathon.pizza.dto.request.AllocateItemRequest;
import goorm.hackathon.pizza.dto.response.*;
import goorm.hackathon.pizza.entity.User;
import java.util.List;

public interface AllocationService {

    /**
     * 특정 정산에 포함된 모든 품목의 목록과 각 품목의 분배 상태를 조회합니다.
     */
    List<AllocationItemResponse> getItemList(Long settlementId);

    /**
     * 특정 품목에 대해 어떤 사용자들이 얼마나 분배받았는지 목록을 조회합니다.
     */
    List<AllocationUserDetailResponse> getAllocationsForItem(Long itemId);

    /**
     * 사용자가 특정 품목을 특정 수량만큼 분배받도록 요청하고 저장합니다.
     * 이 작업 후 해당 사용자의 총 정산 필요 금액(dueAmount) 및 품목 상태가 업데이트됩니다.
     */
    AllocationUserDetailResponse allocateItem(Long settlementId, Long itemId, AllocateItemRequest request, User user);

    /**
     * 특정 정산에서 특정 사용자가 분배받은 내역 전체(개인 영수증)를 조회합니다.
     */
    UserReceiptResponse getUserReceipt(Long settlementId, User user);

    /**
     * 특정 정산의 품목들이 얼마나 분배 완료되었는지 현황(개수)을 조회합니다.
     */
    AllocationStatusCountResponse getAllocationStatusCount(Long settlementId);

    /**
     * 특정 정산의 전체 요약 정보(참여자별 총액 등)를 조회합니다.
     */
    SettlementSummaryResponse getSettlementSummary(Long settlementId, User user);

}