package goorm.hackathon.pizza.service;

import goorm.hackathon.pizza.dto.request.*;
import goorm.hackathon.pizza.dto.response.ItemInfoResponse;
import goorm.hackathon.pizza.dto.response.ItemResponseDto;
import goorm.hackathon.pizza.dto.response.JoinSettlementResponse;
import goorm.hackathon.pizza.dto.response.SettlementResponse;
import goorm.hackathon.pizza.entity.*;
import goorm.hackathon.pizza.entity.Enum.SettlementStatus;
import goorm.hackathon.pizza.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import org.springframework.security.access.AccessDeniedException; // <-- 이 라인을 추가!
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SettlementServiceImpl implements SettlementService {

    private final SettlementRepository settlementRepository;
    private final ReceiptRepository receiptRepository;
    private final ItemRepository itemRepository;
    private final InviteRepository inviteRepository;
    private final ParticipationRepository participationRepository;


    /**
     * 1단계: 빈 껍데기뿐인 임시 정산을 생성합니다.
     */
    @Override
    public SettlementResponse createInitialSettlement(User user) {
        Settlement newSettlement = Settlement.builder()
                .owner(user)
                .title("새로운 정산")
                .status(SettlementStatus.IN_PROGRESS)
                .totalAmount(BigDecimal.ZERO)
                .build();

        Settlement savedSettlement = settlementRepository.save(newSettlement);

        return SettlementResponse.builder()
                .settlementId(savedSettlement.getId())
                .title(savedSettlement.getTitle())
                .status(savedSettlement.getStatus())
                .build();
    }

    /**
     * 2-1단계: 정산 제목을 수정합니다.
     */
    @Override
    public SettlementResponse updateTitle(Long settlementId, UpdateTitleRequestDto request, User user) throws AccessDeniedException {
        Settlement settlement = findSettlementByIdAndCheckOwner(settlementId, user);
        settlement.setTitle(request.getTitle());
        return SettlementResponse.from(settlement);
    }
    /**
     * 2단계: 생성된 정산에 참여 인원을 설정하여 확정합니다.
     */
    @Override
    public SettlementResponse setParticipantLimit(Long settlementId, SetLimitRequestDto request, User user) throws AccessDeniedException {
        Settlement settlement = findSettlementByIdAndCheckOwner(settlementId, user);
        settlement.setParticipantLimit(request.getParticipantLimit());
        return SettlementResponse.from(settlement);
    }


    // 정산 조회 및 소유자 확인을 위한 private 헬퍼 메서드

    private Settlement findSettlementByIdAndCheckOwner(Long settlementId, User user) throws AccessDeniedException {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new EntityNotFoundException("해당 정산을 찾을 수 없습니다."));

        if (!settlement.getOwner().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("해당 정산에 대한 권한이 없습니다.");
        }
        return settlement;
    }

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
    public List<ItemResponseDto> addSettlementItems(Long settlementId, List<ItemRequestDto> itemDtos, User user) throws AccessDeniedException {
        Settlement settlement = findSettlementByIdAndCheckOwner(settlementId, user);

        // 1. DTO 리스트를 Item 엔티티 리스트로 '변환'만 합니다.
        List<Item> newItems = itemDtos.stream()
                .map(dto -> Item.builder()
                        .settlement(settlement)
                        .name(dto.getName())
                        .totalPrice(dto.getTotalPrice())
                        .totalQuantity(dto.getTotalQuantity())
                        .build())
                .collect(Collectors.toList());

        // 2. 변환된 모든 Item 엔티티를 DB에 '일괄 저장'합니다.
        List<Item> savedItems = itemRepository.saveAll(newItems);

        // ★★★ 추가된 로직 ★★★
        // 4. 현재 Settlement 객체의 items 컬렉션에도 새로 저장된 품목들을 추가
        settlement.getItems().addAll(savedItems);

        // 3. 정산 총액을 재계산합니다.
        recalculateTotalAmount(settlement);

        // 4. 저장된 엔티티를 DTO로 변환하여 반환합니다.
        return savedItems.stream()
                .map(item -> ItemResponseDto.builder()
                        .itemId(item.getId())
                        .name(item.getName())
                        .totalPrice(item.getTotalPrice())
                        .totalQuantity(item.getTotalQuantity())
                        .build())
                .collect(Collectors.toList());
    }
    // 총액을 '더하는' 것보다, 현재 아이템 목록 기준으로 '재계산'하는 것이 더 안전합니다.
    private void recalculateTotalAmount(Settlement settlement) {
        BigDecimal totalAmount = settlement.getItems().stream()
                .map(Item::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        settlement.setTotalAmount(totalAmount);
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

    // 정산 생성과 동시에 인원 설정하기
    @Override
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

    // 참여 코드 검증
    public JoinSettlementResponse verifyAndJoinSettlement(String code, User user) {
        // 1. 초대 코드 검증 (기존과 동일)
        Invite invite = inviteRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("참여 코드를 다시 한번 확인해주세요."));

        if (!invite.isActive()) {
            throw new IllegalStateException("만료되었거나 유효하지 않은 초대 코드입니다.");
        }

        Settlement settlement = invite.getSettlement();

        // 2. 중복 참여 및 인원 제한 검증 (기존과 동일)
        if (participationRepository.existsBySettlementAndUser(settlement, user)) {
            throw new IllegalStateException("이미 참여하고 있는 정산입니다.");
        }
        if (settlement.getParticipantLimit() != null && settlement.getParticipations().size() >= settlement.getParticipantLimit()) {
            throw new IllegalStateException("입장 가능한 인원이 초과되었습니다.");
        }

        // 3. Participation 엔티티 생성
        Participation newParticipation = Participation.builder()
                .settlement(settlement)
                .user(user)
                .build();

        // 4. DB에 저장하여 ID와 createdAt 값이 부여된 객체를 받아옴
        Participation savedParticipation = participationRepository.save(newParticipation);

        // 5. 🔥 새로운 정보로 응답 DTO 생성 및 반환
        return JoinSettlementResponse.builder()
                .settlementId(settlement.getId())
                .title(settlement.getTitle())
                .message("정산에 성공적으로 참여했습니다.")
                .participationId(savedParticipation.getId()) // participation ID 추가
                .role(savedParticipation.getRole())         // 참여자의 역할 추가
                .joinedAt(savedParticipation.getCreatedAt())  // 참여 시각 추가
                .build();
    }

    @Override
    public SettlementResponse getSettlementWithCode(String code) {
        return null;
    }

    @Override
    public SettlementResponse getSettlementWithUser() {
        return null;
    }
    // 정산 확정
    @Override
    public SettlementResponse confirmSettlement(Long settlementId, User user) throws AccessDeniedException {
        return null;
    }
}
