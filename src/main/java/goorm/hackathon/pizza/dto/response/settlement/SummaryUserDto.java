// goorm.hackathon.pizza.dto.response.settlement.SummaryUserDto
package goorm.hackathon.pizza.dto.response.settlement;

import java.util.List;

public class SummaryUserDto {
    private Long userId;                    // DB PK
    private String user;                    // 사용자 닉네임
    private boolean isPaid;                 // 납부 여부
    private List<SummaryItemDto> items;     // 사용자별 아이템 리스트

    public SummaryUserDto() {}

    public SummaryUserDto(Long userId,
                          String user,
                          boolean isPaid,
                          List<SummaryItemDto> items) {
        this.userId = userId;
        this.user = user;
        this.isPaid = isPaid;
        this.items = items;
    }

    public Long getUserId() { return userId; }
    public String getUser() { return user; }
    public boolean isPaid() { return isPaid; }
    public List<SummaryItemDto> getItems() { return items; }

    public void setUserId(Long userId) { this.userId = userId; }
    public void setUser(String user) { this.user = user; }
    public void setPaid(boolean paid) { isPaid = paid; }
    public void setItems(List<SummaryItemDto> items) { this.items = items; }
}
