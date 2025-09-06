// src/main/java/goorm/hackathon/pizza/repository/rows/UserItemRow.java
package goorm.hackathon.pizza.repository.rows;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class UserItemRow {
    private final Long userId;          // ← 추가: DB PK
    private final String userNickname;
    private final boolean paid;
    private final String itemName;
    private final int quantity;         // a.quantity
    private final BigDecimal unitPrice; // 계산된 단가

    // (A) 단가가 이미 계산되어 전달될 때 사용하는 생성자
    public UserItemRow(Long userId,
                       String userNickname,
                       boolean paid,
                       String itemName,
                       int quantity,
                       BigDecimal unitPrice) {
        this.userId = userId;
        this.userNickname = userNickname;
        this.paid = paid;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice == null ? BigDecimal.ZERO : unitPrice;
    }

    // (B) totalPrice/totalQuantity 로부터 단가를 계산하는 생성자
    public UserItemRow(Long userId,
                       String userNickname,
                       boolean paid,
                       String itemName,
                       BigDecimal quantity,
                       BigDecimal totalPrice,
                       BigDecimal totalQuantity) {
        this.userId = userId;
        this.userNickname = userNickname;
        this.paid = paid;
        this.itemName = itemName;
        this.quantity = (quantity == null) ? 0 : quantity.intValue();

        if (totalPrice == null || totalQuantity == null || totalQuantity.compareTo(BigDecimal.ZERO) == 0) {
            this.unitPrice = BigDecimal.ZERO;
        } else {
            this.unitPrice = totalPrice.divide(totalQuantity, 0, RoundingMode.HALF_UP); // 소수점 0자리 반올림
        }
    }

    public Long getUserId() { return userId; }
    public String getUserNickname() { return userNickname; }
    public boolean isPaid() { return paid; }
    public String getItemName() { return itemName; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
}
