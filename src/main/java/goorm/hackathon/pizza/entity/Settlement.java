package goorm.hackathon.pizza.entity;

import goorm.hackathon.pizza.entity.Enum.SettlementStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "settlements")
public class Settlement extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settlement_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false, length = 120)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SettlementStatus status = SettlementStatus.IN_PROGRESS;

    private Integer participantLimit;
    private LocalDateTime depositDeadline;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "settlement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items = new ArrayList<>();

    @OneToMany(mappedBy = "settlement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participation> participations = new ArrayList<>();

    @Builder
    public Settlement(User owner, String title, SettlementStatus status, Integer participantLimit, LocalDateTime depositDeadline, BigDecimal totalAmount) {
        this.owner = owner;
        this.title = title;
        this.status = status;
        this.participantLimit = participantLimit;
        this.depositDeadline = depositDeadline;
        this.totalAmount = totalAmount;
    }
}