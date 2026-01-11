package com.aquariux.trading.repository;

import com.aquariux.trading.entities.TradeHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TradeHistoryEntityRepository extends JpaRepository<TradeHistoryEntity, UUID> {
}