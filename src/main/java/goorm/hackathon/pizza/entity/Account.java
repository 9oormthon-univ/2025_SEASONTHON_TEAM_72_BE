package goorm.hackathon.pizza.entity;

import jakarta.persistence.*;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    // 계좌 소유자 (Account N : 1 User)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Column(name = "account_number_hash", nullable = false)
    private String accountNumberHash;

    @Column(name = "account_holder_name", nullable = false)
    private String accountHolderName;
}
