package com.aquariux.trading.repository;

import com.aquariux.trading.entities.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletEntityRepository extends JpaRepository<WalletEntity, Long> {
}