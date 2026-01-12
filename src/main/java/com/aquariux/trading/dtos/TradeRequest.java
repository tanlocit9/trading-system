package com.aquariux.trading.dtos;

import com.aquariux.trading.enums.CryptoPairEnum;
import com.aquariux.trading.enums.OrderSideEnum;
import com.aquariux.trading.validators.EnumValidator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Data Transfer Object for Trade Requests
 */
@Data
public class TradeRequest {
    @Email
    @NotNull(message = "Email is required")
    private String email;

    @EnumValidator(enumClass = CryptoPairEnum.class, message = "Only BTCUSDT and ETHUSDT are supported")
    private String pair;     // BTCUSDT or ETHUSDT

    @EnumValidator(enumClass = OrderSideEnum.class, message = "Only BTCUSDT and ETHUSDT are supported")
    private String side;     // BUY or SELL

    @Positive(message = "Quantity must be greater than zero")
    private BigDecimal quantity;
}