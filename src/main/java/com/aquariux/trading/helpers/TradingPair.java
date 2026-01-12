package com.aquariux.trading.helpers;

import java.util.List;

/**
 * Constants and helpers for supported trading pairs.
 */
public class TradingPair {
    public static final String BTC_USDT = "BTCUSDT";

    public static final String ETH_USDT = "ETHUSDT";

    public static final List<String> SUPPORTED_PAIRS = List.of(BTC_USDT, ETH_USDT);

    public static boolean isSupported(String pair) {
        return SUPPORTED_PAIRS.contains(pair.toUpperCase());
    }
}