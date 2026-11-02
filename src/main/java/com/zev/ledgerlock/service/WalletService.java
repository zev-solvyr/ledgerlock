package com.zev.ledgerlock.service;

import com.zev.ledgerlock.entity.Wallet;
import com.zev.ledgerlock.entity.TransactionHistory;
import com.zev.ledgerlock.repository.TransactionHistoryRepository;
import com.zev.ledgerlock.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;

    @Transactional
    public void transferFunds(String sourceUserId, String destinationUserId, BigDecimal amount){

        if(sourceUserId.equals(destinationUserId)){
            throw new IllegalArgumentException("Cannot transfer money to same wallet");
        }
        if(amount.compareTo(BigDecimal.ZERO)<=0){
            throw new IllegalArgumentException("Transfer amount should be greater than 0");
        }

        // 1. Deadlock Prevention: Always lock the smaller ID first
        boolean isSourceFirst=sourceUserId.compareTo(destinationUserId)<0;

        String firstLockId= isSourceFirst?sourceUserId:destinationUserId;
        String secondLockId= isSourceFirst?destinationUserId:sourceUserId;

        // 2. Acquire Pessimistic Locks in consistent order
        Wallet firstWallet= walletRepository.findByUserIdForUpdate(firstLockId)
                .orElseThrow(()-> new RuntimeException("source wallet not found"));

        Wallet secondWallet= walletRepository.findByUserIdForUpdate(secondLockId)
                .orElseThrow(()-> new RuntimeException("destination wallet not found"));

        // Assign correct roles back to source and destination
        Wallet source= isSourceFirst?firstWallet:secondWallet;

        Wallet destination=isSourceFirst?secondWallet:firstWallet;

        // 3. Business Logic: Check Balance
        if(source.getBalance().compareTo(amount)<0){
            throw new RuntimeException("Insufficient funds");
        }
        // 4. Update Balances
        source.setBalance(source.getBalance().subtract(amount));
        destination.setBalance(destination.getBalance().add(amount));

        // 5. Record the Immutable Audit Trail
        TransactionHistory history= new TransactionHistory();
        history.setSourceWalletId(source.getId());
        history.setDestinationWalletId(destination.getId());
        history.setAmount(amount);
        history.setTransactionType(TransactionHistory.TransactionType.TRANSFER);
        history.setStatus(TransactionHistory.TransactionStatus.SUCCESS);

        // 6. Save State (Hibernate optimizes this into a single batch commit)
        walletRepository.save(source);
        walletRepository.save(destination);
        transactionHistoryRepository.save(history);
    }


}

