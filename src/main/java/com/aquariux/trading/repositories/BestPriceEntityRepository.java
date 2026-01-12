package com.aquariux.trading.repositories;

import com.aquariux.trading.entities.BestPriceEntity;
import com.aquariux.trading.enums.CryptoPairEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BestPriceEntityRepository extends JpaRepository<BestPriceEntity, String> {

    Optional<BestPriceEntity> findByPair(CryptoPairEnum pair);
}