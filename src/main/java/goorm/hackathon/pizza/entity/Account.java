package goorm.hackathon.pizza.entity;

import jakarta.persistence.*;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "nickname", length = 50, nullable = false)
    private String userNickname;

    @Column(nullable = false, length = 40)
    private String bankName;

    @Column(nullable = false, length = 40)
    private String accountNumber;

    @Column(nullable = false)
    private boolean isPrimary = false;

    @Builder
    public Account(User user, String bankName, String userNickname, String accountNumber, boolean isPrimary) {
        this.user = user;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.isPrimary = isPrimary;
        this.userNickname = userNickname;
    }
}