package com.zev.ledgerlock.controller;

import com.zev.ledgerlock.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    public record TransferRequest(String sourceUserId, String destinationUserId, BigDecimal amount){}

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequest request){

        walletService.transferFunds(request.sourceUserId,request.destinationUserId,request.amount);
        return ResponseEntity.ok("Transfer completed successfully");
    }
}
