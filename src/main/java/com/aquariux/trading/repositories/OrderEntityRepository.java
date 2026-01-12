package com.aquariux.trading.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aquariux.trading.entities.OrderEntity;

/**
 * Repository for CRUD operations on `OrderEntity`.
 */
public interface OrderEntityRepository extends JpaRepository<OrderEntity, Long> {
}