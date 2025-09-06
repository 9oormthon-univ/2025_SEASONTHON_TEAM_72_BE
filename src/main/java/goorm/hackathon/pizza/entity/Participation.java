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
    @Builder.Default
    private ParticipantRole role = ParticipantRole.MEMBER;

    @Column(name = "is_paid", nullable = false)
    private boolean isPaid = false;

    @Column(name = "user_nickname", length = 50, nullable = false)
    private String userNickname;  // 닉네임 캐싱

    @Column(precision = 12, scale = 2)
    private BigDecimal dueAmount;

    @Column(precision = 12, scale = 2)
    private BigDecimal paidAmount;

    private LocalDateTime paidAt;

    @Column(nullable = false, updatable = false) // 참여 시각은 생성시에만 기록
    private LocalDateTime createdAt = LocalDateTime.now();

    // 참여 생성 시 닉네임 자동 세팅
    public static Participation create(Settlement settlement, User user, ParticipantRole role) {
        return Participation.builder()
                .settlement(settlement)
                .user(user)
                .role(role)
                .isPaid(false)
                .userNickname(user.getNickname()) // 캐싱
                .build();
    }
}
