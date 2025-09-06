package goorm.hackathon.pizza.entity;

import goorm.hackathon.pizza.entity.Enum.ParticipantRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
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

    @Column(name = "is_paid", nullable = false)
    private boolean isPaid = false;

    @Column(name = "user_nickname", length = 50, nullable = false)
    private String userNickname;  // 닉네임 캐싱

    @Column(precision = 12, scale = 2)
    private BigDecimal dueAmount;

    @Column(precision = 12, scale = 2)
    private BigDecimal paidAmount;

    private LocalDateTime paidAt;

    @Builder
    public Participation(Settlement settlement,
                         User user,
                         ParticipantRole role,
                         boolean isPaid,
                         String userNickname,
                         BigDecimal dueAmount,
                         BigDecimal paidAmount,
                         LocalDateTime paidAt) {
        this.settlement = settlement;
        this.user = user;
        this.role = role;
        this.isPaid = isPaid;
        this.userNickname = userNickname;
        this.dueAmount = dueAmount;
        this.paidAmount = paidAmount;
        this.paidAt = paidAt;
    }

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
