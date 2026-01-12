package com.aquariux.trading.entities;

import com.aquariux.trading.enums.CryptoCurrency;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "wallets")
public class WalletEntity extends BaseAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private CryptoCurrency currency;

    @Column(precision = 18, scale = 8)
    private BigDecimal balance;

    @Column(precision = 18, scale = 8)
    private BigDecimal lockedBalance; // The balance is locked while a buy/sell order is pending.

    @ToString.Exclude
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_entity_id")
    private UserEntity userEntity;

}