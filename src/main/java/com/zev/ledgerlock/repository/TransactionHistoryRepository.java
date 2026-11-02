package com.zev.ledgerlock.repository;

import com.zev.ledgerlock.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, UUID> {
}
