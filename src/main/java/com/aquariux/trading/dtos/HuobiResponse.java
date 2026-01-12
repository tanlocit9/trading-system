package com.aquariux.trading.dtos;

import lombok.Data;

import java.util.List;

/**
 * Wrapper DTO for Huobi API responses containing a list of tickers.
 */
@Data
public class HuobiResponse {
    private List<HuobiTicker> data;
}