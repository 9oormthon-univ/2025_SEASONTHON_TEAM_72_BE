// goorm.hackathon.pizza.dto.response.settlement.SummaryItemDto
package goorm.hackathon.pizza.dto.response.settlement;

public class SummaryItemDto {
    private String name;       // 품목명
    private int quantity;      // 수량
    private int price;         // 단가

    public SummaryItemDto() {}

    public SummaryItemDto(String name, int quantity, int price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public int getPrice() { return price; }

    public void setName(String name) { this.name = name; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(int price) { this.price = price; }
}
