package com.aquariux.trading.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aquariux.trading.entities.TradeHistoryEntity;

/**
 * Repository for accessing historical trade records.
 */
public interface TradeHistoryEntityRepository extends JpaRepository<TradeHistoryEntity, UUID> {
    /**
     * Retrieve trade history for a user ordered descending by execution time.
     */
    List<TradeHistoryEntity> findByUserEntityEmailOrderByExecutedAtDesc(String email);
}