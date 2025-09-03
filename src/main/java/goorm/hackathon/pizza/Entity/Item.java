package goorm.hackathon.pizza.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    // 어떤 정산에 속한 품목인지 (Item N : 1 Settlement)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id", nullable = false)
    private Settlement settlement;

    @Column(nullable = false)
    private String name;

    @Column(name = "total_price")
    private Integer totalPrice;

    @Column(name = "total_quantity")
    private Integer totalQuantity;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
