package com.zev.ledgerlock.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "wallet")
@Getter
@Setter
@NoArgsConstructor
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id",unique = true,nullable = false)
    private String userId;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false,length = 3)
    private String currency;

    @Column(name = "updated_at",insertable = false,updatable = false)
    private Instant updatedAt;
}
