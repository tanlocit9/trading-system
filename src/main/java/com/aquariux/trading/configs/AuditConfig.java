package com.aquariux.trading.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuration enabling JPA auditing support.
 * <p>Used to populate audit fields such as createdAt and updatedAt.</p>
 */
@Configuration
@EnableJpaAuditing
public class AuditConfig {
}