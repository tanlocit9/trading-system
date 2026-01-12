package com.aquariux.trading.entities;

import com.aquariux.trading.enums.CryptoPairEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "best_price")
public class BestPriceEntity extends BaseAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private CryptoPairEnum pair;

    @Column(precision = 18, scale = 8)
    private BigDecimal bestBid;

    @Column(precision = 18, scale = 8)
    private BigDecimal bestAsk;
}
