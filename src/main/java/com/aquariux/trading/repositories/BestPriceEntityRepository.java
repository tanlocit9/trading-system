package com.aquariux.trading.repositories;

import com.aquariux.trading.entities.BestPriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BestPriceEntityRepository extends JpaRepository<BestPriceEntity, String> {
}