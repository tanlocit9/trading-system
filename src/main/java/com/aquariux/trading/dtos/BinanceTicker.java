package com.aquariux.trading.dtos;

import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO representing a Binance ticker snapshot containing best bid and ask prices.
 */
@Data
public class BinanceTicker {
    private String symbol;

    private BigDecimal bidPrice;

    private BigDecimal askPrice;
}