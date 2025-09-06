package goorm.hackathon.pizza.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "receipt_items")
public class ReceiptItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id", nullable = false)
    private Receipt receipt;

    @Column(nullable = false)
    private String name; // 품목 이름

    @Column(nullable = false)
    private Integer quantity; // 수량

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price; // 단가 또는 합계 금액

    @Builder
    public ReceiptItem(Receipt receipt, String name, Integer quantity, BigDecimal price) {
        this.receipt = receipt;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }
}