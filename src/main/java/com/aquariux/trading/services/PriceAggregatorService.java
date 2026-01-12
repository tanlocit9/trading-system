package com.aquariux.trading.services;

import com.aquariux.trading.dtos.BinanceTicker;
import com.aquariux.trading.dtos.HuobiResponse;
import com.aquariux.trading.dtos.HuobiTicker;
import com.aquariux.trading.entities.BestPriceEntity;
import com.aquariux.trading.enums.CrytoPairEnum;
import com.aquariux.trading.repositories.BestPriceEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceAggregatorService {

    private final BestPriceEntityRepository bestPriceEntityRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${binance.api.url:https://api.binance.com/api/v3/ticker/bookTicker}")
    private String binanceUrl;

    @Value("${huobi.api.url:https://api.huobi.pro/market/tickers}")
    private String huobiUrl;

    @Scheduled(fixedRate = 10000) // Run every 10 seconds
    public void aggregatePrices() {
        try {
            // 1. Fetch data from Binance
            BinanceTicker[] binanceTickers = restTemplate.getForObject(binanceUrl, BinanceTicker[].class);

            // 2. Fetch data from Huobi
            HuobiResponse huobiResponse = restTemplate.getForObject(huobiUrl, HuobiResponse.class);

            if (binanceTickers == null || huobiResponse == null) {
                return;
            }
            // 3. Process for each pair BTCUSDT and ETHUSDT
            updateBestPrice(CrytoPairEnum.BTCUSDT, binanceTickers, huobiResponse.getData());
            updateBestPrice(CrytoPairEnum.ETHUSDT, binanceTickers, huobiResponse.getData());

            log.info("Successfully updated best prices at: {}", LocalDateTime.now());
        } catch (Exception e) {
            log.error("Error fetching prices from exchanges: {}", e.getMessage());
        }
    }

    private void updateBestPrice(CrytoPairEnum pair, BinanceTicker[] bTickers, List<HuobiTicker> hTickers) {
        // Find price from Binance
        var bPrice = Arrays.stream(bTickers)
                .filter(t -> t.getSymbol().equalsIgnoreCase(pair.toString()))
                .findFirst().orElse(null);

        // Find price from Huobi
        var hPrice = hTickers.stream()
                .filter(t -> t.getSymbol().equalsIgnoreCase(pair.toString()))
                .findFirst().orElse(null);

        if (bPrice != null && hPrice != null) {
            // Best BID (User SELL): Take Max
            BigDecimal bestBid = bPrice.getBidPrice().max(hPrice.getBid());

            // Best ASK (User BUY): Take Min
            BigDecimal bestAsk = bPrice.getAskPrice().min(hPrice.getAsk());

            // Save or update in H2
            BestPriceEntity entity = bestPriceEntityRepository.findByPair(pair.toString())
                    .orElse(new BestPriceEntity());

            entity.setPair(pair.toString());
            entity.setBestBid(bestBid);
            entity.setBestAsk(bestAsk);

            bestPriceEntityRepository.save(entity);
        }
    }
}