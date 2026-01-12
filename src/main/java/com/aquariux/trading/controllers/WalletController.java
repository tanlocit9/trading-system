package com.aquariux.trading.controllers;

import com.aquariux.trading.entities.WalletEntity;
import com.aquariux.trading.repositories.WalletEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller exposing wallet-related operations such as retrieving balances.
 */
@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {
    private final WalletEntityRepository walletRepo;

    @GetMapping("/{email}")
    public ResponseEntity<List<WalletEntity>> getBalances(@PathVariable String email) {
        return ResponseEntity.ok(walletRepo.findByUserEntityEmail(email));
    }
}