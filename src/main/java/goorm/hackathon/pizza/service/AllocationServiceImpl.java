package goorm.hackathon.pizza.service;

import goorm.hackathon.pizza.dto.request.AllocateItemRequest;
import goorm.hackathon.pizza.dto.response.*;
import goorm.hackathon.pizza.entity.*;
import goorm.hackathon.pizza.entity.Enum.AllocationStatus;
import goorm.hackathon.pizza.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AllocationServiceImpl implements AllocationService {

    private final AllocationRepository allocationRepository;
    private final ItemRepository itemRepository;
    private final SettlementRepository settlementRepository;
    private final ParticipationRepository participationRepository;

    /**
     * 특정 정산에 포함된 모든 품목의 목록과 각 품목의 할당 상태를 조회합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public List<AllocationItemResponse> getItemList(Long settlementId) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new EntityNotFoundException("해당 정산을 찾을 수 없습니다."));

        return itemRepository.findAllBySettlement(settlement).stream()
                .map(item -> AllocationItemResponse.builder()
                        .itemId(item.getId())
                        .name(item.getName())
                        .totalPrice(item.getTotalPrice())
                        .totalQuantity(item.getTotalQuantity())
                        .status(item.getStatus())
                        .build())
                .collect(Collectors.toList());
    }


    /**
     * 특정 품목에 어떤 사용자들이 참여했는지(수량, 금액) 목록을 조회합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public List<AllocationUserDetailResponse> getAllocationsForItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("해당 품목을 찾을 수 없습니다."));

        return item.getAllocations().stream()
                .map(alloc -> AllocationUserDetailResponse.builder()
                        .userId(alloc.getParticipation().getUser().getUserId())
                        .userNickname(alloc.getParticipation().getUser().getNickname())
                        .quantity(alloc.getQuantity())
                        .amount(alloc.getAmount())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 사용자가 특정 품목에 원하는 수량만큼 참여(할당)하도록 요청하고 저장합니다.
     */
    @Override
    public AllocationUserDetailResponse allocateItem(Long settlementId, Long itemId, AllocateItemRequest request, User user) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new EntityNotFoundException("해당 정산을 찾을 수 없습니다."));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("해당 품목을 찾을 수 없습니다."));
        Participation participation = participationRepository.findBySettlementAndUser(settlement, user)
                .orElseThrow(() -> new AccessDeniedException("이 정산의 참여자가 아닙니다."));

        // 현재까지 분배된 총 수량 계산
        BigDecimal currentAllocatedQty = item.getAllocations().stream()
                .map(Allocation::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 이번 요청이 총 수량을 초과하는지 검증
        if (currentAllocatedQty.add(request.getQuantity()).compareTo(item.getTotalQuantity()) > 0) {
            throw new IllegalStateException("분배 가능한 총 수량을 초과하여 선택할 수 없습니다.");
        }

        // 개당 가격 계산 및 지불할 금액 계산
        BigDecimal unitPrice = item.getTotalPrice().divide(item.getTotalQuantity(), 2, RoundingMode.HALF_UP);
        BigDecimal amountToPay = unitPrice.multiply(request.getQuantity());

        // 기존 분배 내역이 있으면 수량을 더하고, 없으면 새로 생성
        Allocation allocation = allocationRepository.findByParticipationAndItem(participation, item)
                .orElseGet(() -> Allocation.builder()
                        .participation(participation)
                        .item(item)
                        .quantity(BigDecimal.ZERO)
                        .amount(BigDecimal.ZERO)
                        .build());

        allocation.addQuantityAndAmount(request.getQuantity(), amountToPay);
        Allocation savedAllocation = allocationRepository.save(allocation);

        // 품목 상태 업데이트 및 나머지 금액 처리
        updateItemStatusAndHandleRemainder(item);
        // 참여자(본인)의 총 정산 필요 금액 재계산
        recalculateAndSetDueAmount(participation);

        return AllocationUserDetailResponse.builder()
                .userId(user.getUserId())
                .userNickname(user.getNickname())
                .quantity(savedAllocation.getQuantity())
                .amount(savedAllocation.getAmount())
                .build();
    }

    /**
     * 특정 정산에서 특정 사용자가 참여한 내역 전체(개인 영수증)를 조회합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public UserReceiptResponse getUserReceipt(Long settlementId, User user) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new EntityNotFoundException("해당 정산을 찾을 수 없습니다."));
        Participation participation = participationRepository.findBySettlementAndUser(settlement, user)
                .orElseThrow(() -> new AccessDeniedException("이 정산의 참여자가 아닙니다."));

        List<UserReceiptItemDetail> itemDetails = participation.getAllocations().stream()
                .map(alloc -> UserReceiptItemDetail.builder()
                        .itemId(alloc.getItem().getId())
                        .itemName(alloc.getItem().getName())
                        .myQuantity(alloc.getQuantity())
                        .myDueAmount(alloc.getAmount())
                        .build())
                .collect(Collectors.toList());

        return UserReceiptResponse.builder()
                .userId(user.getUserId())
                .userNickname(user.getNickname())
                .totalDueAmount(participation.getDueAmount())
                .allocatedItems(itemDetails)
                .build();
    }

    /**
     * 특정 정산의 품목들이 얼마나 할당 완료되었는지 현황(개수)을 조회합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public AllocationStatusCountResponse getAllocationStatusCount(Long settlementId) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new EntityNotFoundException("해당 정산을 찾을 수 없습니다."));

        long completedCount = itemRepository.countBySettlementAndStatus(settlement, AllocationStatus.DONE);
        long totalCount = (long) settlement.getItems().size();

        return AllocationStatusCountResponse.builder()
                .completedCount(completedCount)
                .incompleteCount(totalCount - completedCount)
                .totalCount(totalCount)
                .build();
    }

    /**
     * 특정 정산의 전체 요약 정보(참여자별 총액 등)를 조회합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public SettlementSummaryResponse getSettlementSummary(Long settlementId, User user) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new EntityNotFoundException("해당 정산을 찾을 수 없습니다."));

        if (!participationRepository.existsBySettlementAndUser(settlement, user)) {
            throw new AccessDeniedException("이 정산의 참여자가 아닙니다.");
        }

        List<ParticipantSummaryDto> participantSummaries = settlement.getParticipations().stream()
                .map(p -> ParticipantSummaryDto.builder()
                        .userId(p.getUser().getUserId())
                        .userNickname(p.getUser().getNickname())
                        .dueAmount(p.getDueAmount())
                        .isPaid(p.getPaymentStatus())
                        .build())
                .collect(Collectors.toList());

        return SettlementSummaryResponse.builder()
                .settlementId(settlement.getId())
                .title(settlement.getTitle())
                .totalAmount(settlement.getTotalAmount())
                .participants(participantSummaries)
                .build();
    }

    // private 헬퍼 메서드들
    private void updateItemStatusAndHandleRemainder(Item item) {
        List<Allocation> allocations = item.getAllocations();
        BigDecimal totalAllocatedQuantity = allocations.stream()
                .map(Allocation::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalAllocatedQuantity.compareTo(item.getTotalQuantity()) == 0) {
            item.setStatus(AllocationStatus.DONE);

            BigDecimal sumOfAmounts = allocations.stream()
                    .map(Allocation::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal remainder = item.getTotalPrice().subtract(sumOfAmounts);

            if (remainder.compareTo(BigDecimal.ZERO) > 0 && !allocations.isEmpty()) {
                int randomIndex = new Random().nextInt(allocations.size());
                Allocation luckyAllocation = allocations.get(randomIndex);
                luckyAllocation.addQuantityAndAmount(BigDecimal.ZERO, remainder);
                recalculateAndSetDueAmount(luckyAllocation.getParticipation());
            }

        } else if (totalAllocatedQuantity.compareTo(BigDecimal.ZERO) > 0) {
            item.setStatus(AllocationStatus.AWAITING_PAYMENT);
        } else {
            item.setStatus(AllocationStatus.IN_PROGRESS);
        }
    }

    private void recalculateAndSetDueAmount(Participation participation) {
        BigDecimal totalDue = participation.getAllocations().stream()
                .map(Allocation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        participation.setDueAmount(totalDue);
    }
}