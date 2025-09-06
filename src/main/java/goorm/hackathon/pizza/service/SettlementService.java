package goorm.hackathon.pizza.service;

import goorm.hackathon.pizza.dto.request.CreateSettlementRequestDto;
import goorm.hackathon.pizza.dto.request.ItemRequestDto;
import goorm.hackathon.pizza.dto.request.ItemUpdateRequestDto;
import goorm.hackathon.pizza.dto.response.ItemInfoResponse;
import goorm.hackathon.pizza.dto.response.ItemResponseDto;
import goorm.hackathon.pizza.dto.response.SettlementResponse;
import goorm.hackathon.pizza.entity.User;

import java.util.List;

public interface SettlementService {
    // 정산 임시 생성하기
    SettlementResponse createTempSettlement(Long receiptId, User user);
    // 정산 생성하기
    void createSettlement();
    // 정산 인원 설정하기
    SettlementResponse createSettlementWithParticipants(User user, Integer participantLimit,String title);
    // 영수증 없이 정산 생성
    SettlementResponse createSettlementWithItems(CreateSettlementRequestDto request, User user);

    // 정산 품목 추가
    List<ItemResponseDto> addSettlementItems(Long settlementId, List<ItemRequestDto> itemDtos);
    // 정산 품목 목록 조회
    List<ItemInfoResponse> getSettlementItemList(Long settlementId);
    // 정산 품목 수정
    ItemResponseDto updateSettlementItem(Long settlementId, Long itemId, ItemUpdateRequestDto requestDto);

    // 정산 품목 삭제
    void deleteSettlementItem(Long settlementId, Long itemId);
}
