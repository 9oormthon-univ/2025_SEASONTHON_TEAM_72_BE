package goorm.hackathon.pizza.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class PersonalSettlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "personal_settlement_id")
    private Long id;

    // 전체 정산 정보 (PersonalSettlement N : 1 Settlement)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id", nullable = false)
    private Settlement settlement;

    // 어떤 품목에 대한 것인지 (PersonalSettlement N : 1 Item)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    // 참가자 정보 (PersonalSettlement N : 1 User)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private User participant;

    private Integer quantity;

    private Integer amount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "settlement_status")
    private String status; // 예: "입금전", "입금완료"
}
