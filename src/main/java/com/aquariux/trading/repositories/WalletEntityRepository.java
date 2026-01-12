package com.aquariux.trading.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aquariux.trading.entities.WalletEntity;
import com.aquariux.trading.enums.CryptoCurrency;

/**
 * Repository for user wallet persistence and lookups.
 */
public interface WalletEntityRepository extends JpaRepository<WalletEntity, Long> {

    /**
     * Find all wallets belonging to a user by email.
     */
    List<WalletEntity> findByUserEntityEmail(String email);

    /**
     * Find a single wallet by user email and currency.
     */
    Optional<WalletEntity> findByUserEntityEmailAndCurrency(String email, CryptoCurrency currency);
}