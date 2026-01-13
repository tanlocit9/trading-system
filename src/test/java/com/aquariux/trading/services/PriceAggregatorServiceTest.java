package com.aquariux.trading.services;

import com.aquariux.trading.dtos.BinanceTicker;
import com.aquariux.trading.dtos.HuobiResponse;
import com.aquariux.trading.dtos.HuobiTicker;
import com.aquariux.trading.entities.BestPriceEntity;
import com.aquariux.trading.enums.CryptoPairEnum;
import com.aquariux.trading.repositories.BestPriceEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceAggregatorServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private BestPriceEntityRepository bestPriceEntityRepository;

    @InjectMocks
    private PriceAggregatorService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "binanceUrl", "http://mock-binance-url");
        ReflectionTestUtils.setField(service, "huobiUrl", "http://mock-huobi-url");
    }

    @Test
    void testAggregatePrices_success() {
        // Arrange - Binance data
        BigDecimal bBtcBidPrice = new BigDecimal("50000");
        BigDecimal bBtcAskPrice = new BigDecimal("50010");
        BigDecimal bEthBidPrice = new BigDecimal("3000");
        BigDecimal bEthAskPrice = new BigDecimal("3005");

        BinanceTicker btcBinance = new BinanceTicker();
        btcBinance.setSymbol("BTCUSDT");
        btcBinance.setBidPrice(bBtcBidPrice);
        btcBinance.setAskPrice(bBtcAskPrice);

        BinanceTicker ethBinance = new BinanceTicker();
        ethBinance.setSymbol("ETHUSDT");
        ethBinance.setBidPrice(bEthBidPrice);
        ethBinance.setAskPrice(bEthAskPrice);

        BinanceTicker[] binanceTickers = {btcBinance, ethBinance};

        // Arrange - Huobi data (higher bid, lower ask - should win)
        BigDecimal hBtcBidPrice = new BigDecimal("50005");
        BigDecimal hBtcAskPrice = new BigDecimal("50008");
        BigDecimal hEthBidPrice = new BigDecimal("3002");
        BigDecimal hEthAskPrice = new BigDecimal("3003");

        HuobiTicker btcHuobi = new HuobiTicker();
        btcHuobi.setSymbol("btcusdt");
        btcHuobi.setBid(hBtcBidPrice);
        btcHuobi.setAsk(hBtcAskPrice);

        HuobiTicker ethHuobi = new HuobiTicker();
        ethHuobi.setSymbol("ethusdt");
        ethHuobi.setBid(hEthBidPrice);
        ethHuobi.setAsk(hEthAskPrice);

        HuobiResponse huobiResponse = new HuobiResponse();
        huobiResponse.setData(List.of(btcHuobi, ethHuobi));

        // Mock RestTemplate responses - use anyString() to avoid calling real APIs
        when(restTemplate.getForObject(
                anyString(),
                eq(BinanceTicker[].class)
        )).thenReturn(binanceTickers);

        when(restTemplate.getForObject(
                anyString(),
                eq(HuobiResponse.class)
        )).thenReturn(huobiResponse);

        // Mock bestPriceEntityRepository to return empty (new prices)
        when(bestPriceEntityRepository.findByPair(any(CryptoPairEnum.class)))
                .thenReturn(Optional.empty());

        when(bestPriceEntityRepository.save(any(BestPriceEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        service.aggregatePrices();

        // Assert - Verify bestPriceEntityRepository save was called twice (BTC and ETH)
        ArgumentCaptor<BestPriceEntity> captor = ArgumentCaptor.forClass(BestPriceEntity.class);
        verify(bestPriceEntityRepository, times(2)).save(captor.capture());

        List<BestPriceEntity> savedEntities = captor.getAllValues();

        // Find BTC entity
        BestPriceEntity btcEntity = savedEntities.stream()
                .filter(e -> e.getPair() == CryptoPairEnum.BTCUSDT)
                .findFirst()
                .orElseThrow(() -> new AssertionError("BTC entity not saved"));

        // Best Bid (what we can sell for) = Max(50000, 50005) = 50005 (Huobi)
        // Best Ask (what we can buy for) = Min(50010, 50008) = 50008 (Huobi)
        assertEquals(hBtcBidPrice, btcEntity.getBestBid(), "BTC Best Bid should be from Huobi");
        assertEquals(hBtcAskPrice, btcEntity.getBestAsk(), "BTC Best Ask should be from Huobi");
        assertEquals(CryptoPairEnum.BTCUSDT, btcEntity.getPair());

        // Find ETH entity
        BestPriceEntity ethEntity = savedEntities.stream()
                .filter(e -> e.getPair() == CryptoPairEnum.ETHUSDT)
                .findFirst()
                .orElseThrow(() -> new AssertionError("ETH entity not saved"));

        // Best Bid = Max(3000, 3002) = 3002 (Huobi)
        // Best Ask = Min(3005, 3003) = 3003 (Huobi)
        assertEquals(hEthBidPrice, ethEntity.getBestBid(), "ETH Best Bid should be from Huobi");
        assertEquals(hEthAskPrice, ethEntity.getBestAsk(), "ETH Best Ask should be from Huobi");
        assertEquals(CryptoPairEnum.ETHUSDT, ethEntity.getPair());

        // Verify RestTemplate was called
        verify(restTemplate, times(1)).getForObject(anyString(), eq(BinanceTicker[].class));
        verify(restTemplate, times(1)).getForObject(anyString(), eq(HuobiResponse.class));
    }

    @Test
    void testAggregatePrices_whenBinanceReturnsNull_noActions() {
        // Arrange - Binance returns null
        when(restTemplate.getForObject(anyString(), eq(BinanceTicker[].class)))
                .thenReturn(null);

        // Huobi returns valid data
        HuobiTicker btcHuobi = new HuobiTicker();
        btcHuobi.setSymbol("btcusdt");
        btcHuobi.setBid(new BigDecimal("50000"));
        btcHuobi.setAsk(new BigDecimal("50010"));

        HuobiResponse huobiResponse = new HuobiResponse();
        huobiResponse.setData(List.of(btcHuobi));

        when(restTemplate.getForObject(anyString(), eq(HuobiResponse.class)))
                .thenReturn(huobiResponse);

        // Act
        service.aggregatePrices();

        // Assert - No save should happen because binanceTickers is null
        verify(bestPriceEntityRepository, never()).save(any(BestPriceEntity.class));
    }

    @Test
    void testAggregatePrices_whenHuobiReturnsNull_noActions() {
        // Arrange - Huobi returns null
        BinanceTicker btcBinance = new BinanceTicker();
        btcBinance.setSymbol("BTCUSDT");
        btcBinance.setBidPrice(new BigDecimal("50000"));
        btcBinance.setAskPrice(new BigDecimal("50010"));

        when(restTemplate.getForObject(anyString(), eq(BinanceTicker[].class)))
                .thenReturn(new BinanceTicker[]{btcBinance});

        when(restTemplate.getForObject(anyString(), eq(HuobiResponse.class)))
                .thenReturn(null);

        // Act
        service.aggregatePrices();

        // Assert - No save should happen because huobiResponse is null
        verify(bestPriceEntityRepository, never()).save(any(BestPriceEntity.class));
    }

    @Test
    void testAggregatePrices_whenBothReturnNull_noActions() {
        // Arrange - Both return null
        when(restTemplate.getForObject(anyString(), eq(BinanceTicker[].class)))
                .thenReturn(null);
        when(restTemplate.getForObject(anyString(), eq(HuobiResponse.class)))
                .thenReturn(null);

        // Act
        service.aggregatePrices();

        // Assert - No save should happen
        verify(bestPriceEntityRepository, never()).save(any(BestPriceEntity.class));
    }

    @Test
    void testAggregatePrices_whenBinanceReturnsEmptyArray_noActions() {
        // Arrange - Binance returns empty array
        when(restTemplate.getForObject(anyString(), eq(BinanceTicker[].class)))
                .thenReturn(new BinanceTicker[0]);  // Empty array

        HuobiResponse huobiResponse = new HuobiResponse();
        huobiResponse.setData(List.of());  // Empty list

        when(restTemplate.getForObject(anyString(), eq(HuobiResponse.class)))
                .thenReturn(huobiResponse);

        // Act
        service.aggregatePrices();

        // Assert - No save because no matching pairs found
        verify(bestPriceEntityRepository, never()).save(any(BestPriceEntity.class));
    }

    @Test
    void testAggregatePrices_whenOnlyBinanceHasBTC_noActions() {
        // Arrange - Only Binance has BTC data, Huobi is empty
        BinanceTicker btcBinance = new BinanceTicker();
        btcBinance.setSymbol("BTCUSDT");
        btcBinance.setBidPrice(new BigDecimal("50000"));
        btcBinance.setAskPrice(new BigDecimal("50010"));

        when(restTemplate.getForObject(anyString(), eq(BinanceTicker[].class)))
                .thenReturn(new BinanceTicker[]{btcBinance});

        HuobiResponse huobiResponse = new HuobiResponse();
        huobiResponse.setData(List.of());  // No BTC in Huobi

        when(restTemplate.getForObject(anyString(), eq(HuobiResponse.class)))
                .thenReturn(huobiResponse);

        // Act
        service.aggregatePrices();

        // Assert - No save because both exchanges need data (bPrice != null && hPrice != null)
        verify(bestPriceEntityRepository, never()).save(any(BestPriceEntity.class));
    }

    @Test
    void testFetchPrices_whenApiFails_shouldHandleException() {
        // 1. Setup: Force the first API call to throw an exception
        when(restTemplate.getForObject(anyString(), eq(BinanceTicker[].class)))
                .thenThrow(new RestClientException("Binance API is down"));

        // 2. Execute: Call the method containing your try-catch block
        service.aggregatePrices();

        // 3. Verify: Check that the update logic was NEVER called because of the error
        verify(restTemplate, times(1)).getForObject(anyString(), eq(BinanceTicker[].class));
    }
}