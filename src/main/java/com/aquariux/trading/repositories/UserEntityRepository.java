package com.aquariux.trading.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aquariux.trading.entities.UserEntity;

/**
 * Repository for user lookup and persistence operations.
 */
public interface UserEntityRepository extends JpaRepository<UserEntity, UUID> {
    /**
     * Find the first matching `UserEntity` by email.
     */
    Optional<UserEntity> findFirstByEmail(String email);
}