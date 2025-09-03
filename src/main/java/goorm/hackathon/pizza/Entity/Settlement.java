package goorm.hackathon.pizza.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settlement_id")
    private Long id;

    // 총괄자 정보 (Settlement N : 1 User)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private User manager;

    @Column(nullable = false)
    private String title;

    @Column(name = "settlement_status")
    private String status; // 예: "진행중", "완료"

    @Column(name = "participant_count")
    private Integer participantCount;

    @Column(name = "deposit_date")
    private LocalDate depositDate;

    @Column(name = "total_amount")
    private Integer totalAmount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @OneToMany(mappedBy = "settlement")
    private List<SettlementParticipant> settlementParticipants = new ArrayList<>();

    // 정산에 포함된 품목 목록 (Settlement 1 : N Item)
    @OneToMany(mappedBy = "settlement", cascade = CascadeType.ALL)
    private List<Item> items = new ArrayList<>();

    // 개인 정산 목록 (Settlement 1 : N PersonalSettlement)
    @OneToMany(mappedBy = "settlement", cascade = CascadeType.ALL)
    private List<PersonalSettlement> personalSettlements = new ArrayList<>();

    // 영수증 정보 (Settlement 1 : 1 Receipt)
    @OneToOne(mappedBy = "settlement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Receipt receipt;
}
