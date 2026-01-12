package com.aquariux.trading.services;

import com.aquariux.trading.entities.BestPriceEntity;
import com.aquariux.trading.entities.TradeHistoryEntity;
import com.aquariux.trading.entities.UserEntity;
import com.aquariux.trading.entities.WalletEntity;
import com.aquariux.trading.enums.CryptoCurrency;
import com.aquariux.trading.enums.CryptoPairEnum;
import com.aquariux.trading.enums.OrderSideEnum;
import com.aquariux.trading.helpers.EnumUtils;
import com.aquariux.trading.repositories.BestPriceEntityRepository;
import com.aquariux.trading.repositories.TradeHistoryEntityRepository;
import com.aquariux.trading.repositories.UserEntityRepository;
import com.aquariux.trading.repositories.WalletEntityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TradeService {
    private final BestPriceEntityRepository bestPriceRepo;

    private final WalletEntityRepository walletRepo;

    private final TradeHistoryEntityRepository tradeHistoryRepo;

    private final UserEntityRepository userRepo;

    @Transactional
    public TradeHistoryEntity executeTrade(String email, CryptoPairEnum pair, OrderSideEnum side, BigDecimal quantity) {
        UserEntity user = userRepo.findFirstByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        // 1. Get current best prices
        BestPriceEntity bestPrice = bestPriceRepo.findByPair(pair)
                .orElseThrow(() -> new RuntimeException("Cannot find best prices"));

        boolean isBuying = side == OrderSideEnum.BUY;
        BigDecimal price = isBuying ? bestPrice.getBestAsk() : bestPrice.getBestBid();
        BigDecimal totalUsdt = price.multiply(quantity);

        // 2. Check current wallet
        CryptoCurrency quoteCurrency = CryptoCurrency.USDT;
        int startingUSDTPosition = pair.toString().indexOf(quoteCurrency.toString());
        CryptoCurrency baseCurrency = EnumUtils.parse(pair.toString().substring(0, startingUSDTPosition), CryptoCurrency.class).orElseThrow(() -> new RuntimeException("Trading pair is not supported"));

        if (isBuying) {
            // Buy BTC: Minus USDT, plus BTC
            updateBalance(email, quoteCurrency, totalUsdt.negate());
            updateBalance(email, baseCurrency, quantity);
        } else {
            // Sell BTC: Minus BTC, plus USDT
            updateBalance(email, baseCurrency, quantity.negate());
            updateBalance(email, quoteCurrency, totalUsdt);
        }

        // 3. Store trading history
        TradeHistoryEntity history = TradeHistoryEntity.builder()
                .userEntity(user)
                .pair(pair)
                .side(side)
                .price(price)
                .quantity(quantity)
                .totalAmount(totalUsdt)
                .executedAt(LocalDateTime.now())
                .build();

        return tradeHistoryRepo.save(history);
    }

    private void updateBalance(String email, CryptoCurrency currency, BigDecimal amount) {
        WalletEntity wallet = walletRepo.findByUserEntityEmailAndCurrency(email, currency)
                .orElseThrow(() -> new RuntimeException("User don't have " + currency));

        BigDecimal newBalance = wallet.getBalance().add(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Not enough balance");
        }
        wallet.setBalance(newBalance);
        walletRepo.save(wallet);
    }
}