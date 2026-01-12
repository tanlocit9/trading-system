package com.aquariux.trading.entities;

import com.aquariux.trading.enums.CryptoPairEnum;
import com.aquariux.trading.enums.OrderSideEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Historical record of executed trades.
 * <p>Includes pair, side, price, quantity, execution timestamp and reference to the executing user.</p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "trade_history")
public class TradeHistoryEntity extends BaseAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private CryptoPairEnum pair;

    @Enumerated(EnumType.STRING)
    private OrderSideEnum side;

    @Column(precision = 18, scale = 8)
    private BigDecimal price;

    @Column(precision = 18, scale = 8)
    private BigDecimal quantity;

    @Column(precision = 18, scale = 8)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private LocalDateTime executedAt;

    private String source;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_entity_id")
    private UserEntity userEntity;

}