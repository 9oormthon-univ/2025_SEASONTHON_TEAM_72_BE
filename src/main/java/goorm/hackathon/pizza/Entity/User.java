package goorm.hackathon.pizza.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "kakao_id", unique = true, nullable = false)
    private Long kakaoId;

    @Column(nullable = false)
    private String nickname;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "profile_image_url")
    private String profileImage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 사용자가 총괄하는 정산 목록 (User 1 : N Settlement)
    @OneToMany(mappedBy = "manager")
    private List<Settlement> managedSettlements = new ArrayList<>();

    // 사용자가 참여하는 정산 목록 (User N : M Settlement)
    @ManyToMany(mappedBy = "participants")
    private List<Settlement> settlements = new ArrayList<>();

    // 사용자의 계좌 목록 (User 1 : N Account)
    @OneToMany(mappedBy = "user")
    private List<Account> accounts = new ArrayList<>();

    // 사용자의 개인 정산 목록 (User 1 : N PersonalSettlement)
    @OneToMany(mappedBy = "participant")
    private List<PersonalSettlement> personalSettlements = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<SettlementParticipant> settlementParticipants = new ArrayList<>();
}
