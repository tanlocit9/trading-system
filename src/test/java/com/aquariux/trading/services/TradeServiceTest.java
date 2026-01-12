package com.aquariux.trading.services;

import com.aquariux.trading.entities.BestPriceEntity;
import com.aquariux.trading.entities.TradeHistoryEntity;
import com.aquariux.trading.entities.UserEntity;
import com.aquariux.trading.entities.WalletEntity;
import com.aquariux.trading.enums.CryptoCurrency;
import com.aquariux.trading.enums.CryptoPairEnum;
import com.aquariux.trading.enums.OrderSideEnum;
import com.aquariux.trading.repositories.BestPriceEntityRepository;
import com.aquariux.trading.repositories.TradeHistoryEntityRepository;
import com.aquariux.trading.repositories.UserEntityRepository;
import com.aquariux.trading.repositories.WalletEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradeServiceTest {

    @Mock
    BestPriceEntityRepository bestPriceRepo;

    @Mock
    WalletEntityRepository walletRepo;

    @Mock
    TradeHistoryEntityRepository tradeHistoryRepo;

    @Mock
    UserEntityRepository userRepo;

    @InjectMocks
    TradeService tradeService;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setEmail("user@example.com");
    }

    @Test
    void executeBuyTrade_success_updatesWalletsAndSavesHistory() {
        BestPriceEntity bestPrice = new BestPriceEntity();
        bestPrice.setPair(CryptoPairEnum.BTCUSDT);
        bestPrice.setBestAsk(new BigDecimal("50500"));

        when(userRepo.findFirstByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(bestPriceRepo.findByPair(CryptoPairEnum.BTCUSDT)).thenReturn(Optional.of(bestPrice));

        WalletEntity usdt = new WalletEntity();
        usdt.setCurrency(CryptoCurrency.USDT);
        usdt.setBalance(new BigDecimal("200000"));

        WalletEntity btc = new WalletEntity();
        btc.setCurrency(CryptoCurrency.BTC);
        btc.setBalance(BigDecimal.ZERO);

        when(walletRepo.findByUserEntityEmailAndCurrency("user@example.com", CryptoCurrency.USDT))
                .thenReturn(Optional.of(usdt));
        when(walletRepo.findByUserEntityEmailAndCurrency("user@example.com", CryptoCurrency.BTC))
                .thenReturn(Optional.of(btc));

        when(tradeHistoryRepo.save(any(TradeHistoryEntity.class))).thenAnswer(i -> i.getArgument(0));

        var history = tradeService.executeTrade("user@example.com", CryptoPairEnum.BTCUSDT, OrderSideEnum.BUY, new BigDecimal("2"));

        // USDT deducted: 50500 * 2 = 101000 -> remaining 99000
        assertEquals(new BigDecimal("99000"), usdt.getBalance());
        assertEquals(new BigDecimal("2"), btc.getBalance());

        // Validate quantity and price stored in history
        assertEquals(new BigDecimal("2"), history.getQuantity());
        assertEquals(new BigDecimal("50500"), history.getPrice());

        verify(walletRepo, times(2)).save(any(WalletEntity.class));
        verify(tradeHistoryRepo, times(1)).save(any(TradeHistoryEntity.class));
    }

    @Test
    void executeSellTrade_insufficientBalance_throws() {
        BestPriceEntity bestPrice = new BestPriceEntity();
        bestPrice.setPair(CryptoPairEnum.BTCUSDT);
        bestPrice.setBestBid(new BigDecimal("50000"));

        when(userRepo.findFirstByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(bestPriceRepo.findByPair(CryptoPairEnum.BTCUSDT)).thenReturn(Optional.of(bestPrice));

        WalletEntity btc = new WalletEntity();
        btc.setCurrency(CryptoCurrency.BTC);
        btc.setBalance(new BigDecimal("1"));

        when(walletRepo.findByUserEntityEmailAndCurrency("user@example.com", CryptoCurrency.BTC))
                .thenReturn(Optional.of(btc));

        assertThrows(RuntimeException.class, () ->
                tradeService.executeTrade("user@example.com", CryptoPairEnum.BTCUSDT, OrderSideEnum.SELL, new BigDecimal("2")));
    }

    @Test
    void executeSellTrade_success_updatesWalletsAndSavesHistory() {
        BestPriceEntity bestPrice = new BestPriceEntity();
        bestPrice.setPair(CryptoPairEnum.BTCUSDT);
        bestPrice.setBestBid(new BigDecimal("50000"));

        when(userRepo.findFirstByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(bestPriceRepo.findByPair(CryptoPairEnum.BTCUSDT)).thenReturn(Optional.of(bestPrice));

        WalletEntity usdt = new WalletEntity();
        usdt.setCurrency(CryptoCurrency.USDT);
        usdt.setBalance(new BigDecimal("200000"));

        WalletEntity btc = new WalletEntity();
        btc.setCurrency(CryptoCurrency.BTC);
        btc.setBalance(new BigDecimal("3"));

        when(walletRepo.findByUserEntityEmailAndCurrency("user@example.com", CryptoCurrency.USDT))
                .thenReturn(Optional.of(usdt));
        when(walletRepo.findByUserEntityEmailAndCurrency("user@example.com", CryptoCurrency.BTC))
                .thenReturn(Optional.of(btc));

        when(tradeHistoryRepo.save(any(TradeHistoryEntity.class))).thenAnswer(i -> i.getArgument(0));

        var history = tradeService.executeTrade("user@example.com", CryptoPairEnum.BTCUSDT, OrderSideEnum.SELL, new BigDecimal("2"));

        // USDT added: 50000 * 2 = 100000 -> remaining 100000
        assertEquals(new BigDecimal("300000"), usdt.getBalance());
        assertEquals(new BigDecimal("1"), btc.getBalance());

        // Validate quantity and price stored in history
        assertEquals(new BigDecimal("2"), history.getQuantity());
        assertEquals(new BigDecimal("50000"), history.getPrice());

        verify(walletRepo, times(2)).save(any(WalletEntity.class));
        verify(tradeHistoryRepo, times(1)).save(any(TradeHistoryEntity.class));
    }

    @Test
    void executeTrade_userNotFound_throws() {
        when(userRepo.findFirstByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                tradeService.executeTrade("missing@example.com", CryptoPairEnum.BTCUSDT, OrderSideEnum.BUY, new BigDecimal("1")));
    }
}
