package com.aquariux.trading.dtos;

import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO representing a Huobi ticker snapshot with best bid and ask values.
 */
@Data
public class HuobiTicker {
    private String symbol;

    private BigDecimal bid;

    private BigDecimal ask;
}
