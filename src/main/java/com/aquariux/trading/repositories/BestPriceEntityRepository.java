package com.aquariux.trading.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aquariux.trading.entities.BestPriceEntity;
import com.aquariux.trading.enums.CryptoPairEnum;

public interface BestPriceEntityRepository extends JpaRepository<BestPriceEntity, String> {
    /**
     * Find best price record for a given trading pair.
     */
    Optional<BestPriceEntity> findByPair(CryptoPairEnum pair);
}