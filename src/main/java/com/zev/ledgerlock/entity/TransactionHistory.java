package com.zev.ledgerlock.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transaction_history")
@Getter
@Setter
@NoArgsConstructor
public class TransactionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "source_wallet_id")
    private UUID sourceWalletId;

    @Column(name = "destination_wallet_id")
    private UUID destinationWalletId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type",nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(name = "created_at",insertable = false,updatable = false)
    private Instant createdAt;

    public enum TransactionType{
        DEPOSIT, WITHDRAWAL, TRANSFER
    }
    public enum TransactionStatus{
        PENDING,SUCCESS,FAILED
    }

}
