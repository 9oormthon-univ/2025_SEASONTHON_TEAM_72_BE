// goorm.hackathon.pizza.repository.rows.OverallItemRow
package goorm.hackathon.pizza.repository.rows;

import java.math.BigDecimal;

public class OverallItemRow {
    private final String itemName;
    private final BigDecimal quantitySum;
    private final BigDecimal unitPrice;
    private final BigDecimal totalPrice;

    public OverallItemRow(String itemName,
                          BigDecimal quantitySum,
                          BigDecimal unitPrice,
                          BigDecimal totalPrice) {
        this.itemName = itemName;
        this.quantitySum = quantitySum;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    public String getItemName() { return itemName; }
    public BigDecimal getQuantitySum() { return quantitySum; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getTotalPrice() { return totalPrice; }

    // 서비스에서 row.quantitySum() 형태로 부르고 있다면 보조 메서드도 추가
    public BigDecimal quantitySum() { return quantitySum; }
}
