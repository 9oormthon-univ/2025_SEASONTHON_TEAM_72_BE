// src/main/java/goorm/hackathon/pizza/dto/response/settlement/SettlementSummaryDto.java
package goorm.hackathon.pizza.dto.response.settlement;

import java.util.List;

public class SettlementSummaryDto {
    private String title;                   // 정산 제목
    private Long settlementId;              // 정산 ID
    private Long ownerId;                   // 총괄자 ID
    private List<SummaryUserDto> users;     // 참여자별 요약

    public SettlementSummaryDto() {}

    public SettlementSummaryDto(String title, Long ownerId, Long settlementId, List<SummaryUserDto> users) {
        this.title = title;
        this.settlementId = settlementId;
        this.users = users;
        this.ownerId = ownerId;
    }

    public String getTitle() { return title; }
    public Long getSettlementId() { return settlementId; }
    public List<SummaryUserDto> getUsers() { return users; }
    public Long getOwnerId() { return ownerId; }

    public void setTitle(String title) { this.title = title; }
    public void setSettlementId(Long settlementId) { this.settlementId = settlementId; }
    public void setUsers(List<SummaryUserDto> users) { this.users = users; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}
