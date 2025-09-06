package goorm.hackathon.pizza.service;

import goorm.hackathon.pizza.dto.request.CreateSettlementRequestDto;
import goorm.hackathon.pizza.dto.request.ItemRequestDto;
import goorm.hackathon.pizza.dto.request.ItemUpdateRequestDto;
import goorm.hackathon.pizza.dto.response.ItemInfoResponse;
import goorm.hackathon.pizza.dto.response.ItemResponseDto;
import goorm.hackathon.pizza.dto.response.SettlementResponse;
import goorm.hackathon.pizza.entity.Enum.SettlementStatus;
import goorm.hackathon.pizza.entity.Item;
import goorm.hackathon.pizza.entity.Receipt;
import goorm.hackathon.pizza.entity.Settlement;
import goorm.hackathon.pizza.entity.User;
import goorm.hackathon.pizza.repository.ItemRepository;
import goorm.hackathon.pizza.repository.ReceiptRepository;
import goorm.hackathon.pizza.repository.SettlementRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SettlementServiceImpl implements SettlementService {

    private final SettlementRepository settlementRepository;
    private final ReceiptRepository receiptRepository;
    private final ItemRepository itemRepository;


    // 정산 임시 생성하기
    @Override
    @Transactional
    public SettlementResponse createTempSettlement(Long receiptId, User user) {

        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new EntityNotFoundException("해당 영수증을 찾을 수 없습니다."));

        if (receipt.getSettlement() != null) {
            throw new IllegalStateException("이미 정산에 포함된 영수증입니다.");
        }

        Settlement newSettlement = Settlement.builder()
                .owner(user)
                .title("새로운 정산")
                .status(SettlementStatus.IN_PROGRESS)
                .totalAmount(BigDecimal.ZERO)
                .build();

        Settlement savedSettlement = settlementRepository.save(newSettlement);

        receipt.setSettlement(savedSettlement);

        return SettlementResponse.builder()
                .settlementId(savedSettlement.getId())
                .title(savedSettlement.getTitle())
                .status(savedSettlement.getStatus())
                .build();
    }
    // 정산 생성하기
    @Override
    public void createSettlement() {

    }
    // 정산 품목 추가
    @Transactional
    public List<ItemResponseDto> addSettlementItems(Long settlementId, List<ItemRequestDto> itemDtos) {
        // 1. 정산 엔티티 조회
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new EntityNotFoundException("해당 정산을 찾을 수 없습니다."));

        // 2. 받은 품목 리스트를 Item 엔티티로 변환 및 저장
        List<Item> newItems = itemDtos.stream()
                .map(dto -> {
                    Item newItem = Item.builder()
                            .settlement(settlement) // 정산과 품목 연관관계 설정
                            .name(dto.getName())
                            .totalPrice(dto.getTotalPrice())
                            .totalQuantity(dto.getTotalQuantity())
                            .build();
                    return itemRepository.save(newItem);
                })
                .collect(Collectors.toList());

        // 3. 정산 총액 업데이트
        // 현재 정산의 총액에 새로 추가된 품목들의 가격을 더합니다.
        BigDecimal totalNewAmount = newItems.stream()
                .map(Item::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal currentTotalAmount = settlement.getTotalAmount() != null ? settlement.getTotalAmount() : BigDecimal.ZERO;
        settlement.setTotalAmount(currentTotalAmount.add(totalNewAmount));
        // settlementRepository.save(settlement); 는 @Transactional에 의해 자동으로 수행됩니다.

        // 4. 저장된 엔티티를 DTO로 변환하여 반환
        return newItems.stream()
                .map(item -> ItemResponseDto.builder()
                        .itemId(item.getId())
                        .name(item.getName())
                        .totalPrice(item.getTotalPrice())
                        .totalQuantity(item.getTotalQuantity())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 영수증 없이 수동으로 품목을 입력받아 정산을 생성합니다.
     */
    @Transactional
    public SettlementResponse createSettlementWithItems(CreateSettlementRequestDto request, User user) {
        // 1. DTO에 포함된 품목들의 총액을 먼저 계산
        BigDecimal totalAmount = request.getItems().stream()
                .map(ItemRequestDto::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. 계산된 총액을 포함하여 Settlement 엔티티를 생성
        Settlement newSettlement = Settlement.builder()
                .owner(user)
                .title(request.getTitle())
                .status(SettlementStatus.IN_PROGRESS)
                .participantLimit(request.getParticipantLimit())
                .totalAmount(totalAmount)
                .build();

        // 3. DTO의 품목들을 Item 엔티티로 변환하고, 생성된 Settlement와 연결
        List<Item> newItems = request.getItems().stream()
                .map(dto -> Item.builder()
                        .settlement(newSettlement)
                        .name(dto.getName())
                        .totalPrice(dto.getTotalPrice())
                        .totalQuantity(dto.getTotalQuantity())
                        .build())
                .collect(Collectors.toList());

        // 4. Settlement에 Item 목록을 설정
        newSettlement.setItems(newItems);

        // 5. Settlement를 저장
        Settlement savedSettlement = settlementRepository.save(newSettlement);

        // 6. 응답 DTO
        return SettlementResponse.builder()
                .settlementId(savedSettlement.getId())
                .title(savedSettlement.getTitle())
                .status(savedSettlement.getStatus())
                .build();
    }
    // 정산 품목 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<ItemInfoResponse> getSettlementItemList(Long settlementId) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new EntityNotFoundException("해당 정산을 찾을 수 없습니다."));

        return settlement.getItems().stream()
                .map(item -> ItemInfoResponse.builder()
                        .itemId(item.getId())
                        .name(item.getName())
                        .totalPrice(item.getTotalPrice())
                        .totalQuantity(item.getTotalQuantity())
                        .build())
                .collect(Collectors.toList());
    }

    // 정산 품목 수정
    @Override
    @Transactional
    public ItemResponseDto updateSettlementItem(Long settlementId, Long itemId, ItemUpdateRequestDto requestDto) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new EntityNotFoundException("해당 정산을 찾을 수 없습니다."));

        Item itemToUpdate = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("해당 품목을 찾을 수 없습니다."));

        if (!itemToUpdate.getSettlement().getId().equals(settlement.getId())) {
            throw new SecurityException("해당 정산에 속한 품목이 아닙니다.");
        }

        itemToUpdate.setName(requestDto.getName());
        itemToUpdate.setTotalPrice(requestDto.getTotalPrice());
        itemToUpdate.setTotalQuantity(requestDto.getTotalQuantity());

        // 정산 총액 재계산 (이제 필터링 없이 합산만 합니다)
        recalculateTotalAmount(settlement);

        return ItemResponseDto.builder()
                .itemId(itemToUpdate.getId())
                .name(itemToUpdate.getName())
                .totalPrice(itemToUpdate.getTotalPrice())
                .totalQuantity(itemToUpdate.getTotalQuantity())
                .build();
    }

    // 정산 품목 삭제
    @Override
    @Transactional
    public void deleteSettlementItem(Long settlementId, Long itemId) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new EntityNotFoundException("해당 정산을 찾을 수 없습니다."));

        Item itemToDelete = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("해당 품목을 찾을 수 없습니다."));

        if (!itemToDelete.getSettlement().getId().equals(settlement.getId())) {
            throw new SecurityException("해당 정산에 속한 품목이 아닙니다.");
        }

        // ★★★ 수정된 부분 ★★★
        // 1. Settlement의 items 컬렉션에서도 해당 아이템을 제거 (메모리 상태 일치)
        settlement.getItems().remove(itemToDelete);

        // 2. DB에서 품목 삭제
        itemRepository.delete(itemToDelete);

        // 3. 정산 총액 재계산
        recalculateTotalAmount(settlement);
    }

    // 정산 총액을 다시 계산하는 private 헬퍼 메서드 (수정 후)
    private void recalculateTotalAmount(Settlement settlement) {
        // 현재 settlement 객체가 메모리에서 가지고 있는 items 목록의 총합을 계산
        BigDecimal newTotalAmount = settlement.getItems().stream()
                .map(Item::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        settlement.setTotalAmount(newTotalAmount);
    }
    // 정산 생성과 동시에 인원 설정하기
    public SettlementResponse createSettlementWithParticipants(User user, Integer participantLimit, String title) {
        Settlement newSettlement = Settlement.builder()
                .owner(user)
                .title(title) // 요청으로부터 받은 제목 사용
                .status(SettlementStatus.IN_PROGRESS)
                .participantLimit(participantLimit)
                .totalAmount(BigDecimal.ZERO)
                .build();

        Settlement savedSettlement = settlementRepository.save(newSettlement);

        return SettlementResponse.builder()
                .settlementId(savedSettlement.getId())
                .title(savedSettlement.getTitle())
                .status(savedSettlement.getStatus())
                .build();
    }



}
