package com.aquariux.trading.repository;

import com.aquariux.trading.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderEntityRepository extends JpaRepository<OrderEntity, Long> {
}