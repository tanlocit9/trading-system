package com.aquariux.trading.repositories;

import com.aquariux.trading.entities.WalletEntity;
import com.aquariux.trading.enums.CryptoCurrency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletEntityRepository extends JpaRepository<WalletEntity, Long> {

    List<WalletEntity> findByUserEntityEmail(String email);

    Optional<WalletEntity> findByUserEntityEmailAndCurrency(String email, CryptoCurrency currency);
}