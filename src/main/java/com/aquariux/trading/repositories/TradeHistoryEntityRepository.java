package com.aquariux.trading.repositories;

import com.aquariux.trading.entities.TradeHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TradeHistoryEntityRepository extends JpaRepository<TradeHistoryEntity, UUID> {
    List<TradeHistoryEntity> findByUserEntityEmailOrderByExecutedAtDesc(String email);
}