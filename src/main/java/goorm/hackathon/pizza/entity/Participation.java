package goorm.hackathon.pizza.entity;

import goorm.hackathon.pizza.entity.Enum.ParticipantRole;
import goorm.hackathon.pizza.entity.Enum.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.units.qual.A;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "participation", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"settlement_id", "user_id"})
})
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id", nullable = false)
    private Settlement settlement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipantRole role = ParticipantRole.MEMBER;

    @Column(nullable = false)
    private Boolean paymentStatus;

    @Column(precision = 12, scale = 2)
    private BigDecimal dueAmount;

    @Column(precision = 12, scale = 2)
    private BigDecimal paidAmount;

    private LocalDateTime paidAt;

}