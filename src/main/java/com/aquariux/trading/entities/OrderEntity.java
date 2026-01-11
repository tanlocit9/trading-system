package com.aquariux.trading.entities;

import com.aquariux.trading.enums.OrderSideEnum;
import com.aquariux.trading.enums.OrderStatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "orders")
public class OrderEntity extends BaseAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    @Column(nullable = false)
    private String pair;

    @Enumerated(EnumType.STRING)
    private OrderSideEnum side;

    @Enumerated(EnumType.STRING)
    private OrderStatusEnum status;

    private BigDecimal price;

    private BigDecimal quantity;

    private BigDecimal totalAmount;
}