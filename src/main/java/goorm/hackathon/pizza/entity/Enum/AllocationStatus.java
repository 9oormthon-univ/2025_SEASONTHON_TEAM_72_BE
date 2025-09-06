package goorm.hackathon.pizza.entity.Enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AllocationStatus {
    IN_PROGRESS("정산 항목 입력 중"),
    AWAITING_PAYMENT("입금 대기 중"),
    DONE("정산 완료");
    private final String title;
}
