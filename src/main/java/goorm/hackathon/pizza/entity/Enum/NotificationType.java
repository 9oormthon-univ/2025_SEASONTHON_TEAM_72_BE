package goorm.hackathon.pizza.entity.Enum;

public enum NotificationType {
    // 사용자
    SETTLEMENT_COMPLETED,       // 정산 완료 (사용자)
    DEPOSIT_REQUEST,            // 독촉/입금 요청 (사용자에게)
    DEPOSIT_CANCELED,           // 입금 취소 (사용자)
    // 관리자
    DEPOSIT_CONFIRMED,          // 누가 입금함 (관리자에게)
    SETTLEMENT_COMPLETE_REQUEST // 정산 완료 처리 요청 (관리자에게)
}
