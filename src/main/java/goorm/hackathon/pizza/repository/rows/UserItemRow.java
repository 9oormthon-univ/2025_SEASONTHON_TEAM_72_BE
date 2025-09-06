// src/main/java/goorm/hackathon/pizza/repository/rows/UserItemRow.java
package goorm.hackathon.pizza.repository.rows;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class UserItemRow {
    private final Long userId;       // users.user_id
    private final String userNickname;
    private final boolean paid;
    private final String itemName;
    private final int quantity;
    private final BigDecimal unitPrice;

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
            this.unitPrice = totalPrice.divide(totalQuantity, 0, RoundingMode.HALF_UP);
        }
    }

    public Long getUserId() { return userId; }
    public String getUserNickname() { return userNickname; }
    public boolean isPaid() { return paid; }
    public String getItemName() { return itemName; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
}
