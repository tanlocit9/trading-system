package com.aquariux.trading.configs;

import com.aquariux.trading.entities.BestPriceEntity;
import com.aquariux.trading.entities.UserEntity;
import com.aquariux.trading.entities.WalletEntity;
import com.aquariux.trading.enums.CryptoCurrency;
import com.aquariux.trading.enums.CryptoPairEnum;
import com.aquariux.trading.repositories.BestPriceEntityRepository;
import com.aquariux.trading.repositories.UserEntityRepository;
import com.aquariux.trading.repositories.WalletEntityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Transactional
public class DataConfig implements CommandLineRunner {

    private final UserEntityRepository userRepository;

    private final WalletEntityRepository walletRepository;

    private final BestPriceEntityRepository bestPriceRepository;

    @Override
    public void run(String... args) {
        // 1. Create a Default Test User
        UserEntity testUser = new UserEntity();
        testUser.setUsername("crypto_trader_01");
        testUser.setEmail("trader@example.com");
        // In a real app, passwords should be BCrypt encoded
        testUser.setPassword("secure_password_123");
        userRepository.save(testUser);

        System.out.println("--- Test User Created with ID: " + testUser.getId() + " ---");

        // 2. Initialize Wallets with starting balances
        // Give the user 50,000 USDT to start trading
        saveWallet(testUser, CryptoCurrency.USDT, new BigDecimal("50000.00"));
        // Give small amounts of BTC and ETH for selling tests
        saveWallet(testUser, CryptoCurrency.BTC, new BigDecimal("1.5"));
        saveWallet(testUser, CryptoCurrency.ETH, new BigDecimal("10.0"));

        // 3. Initialize BestPrice entries to prevent NullPointerExceptions in APIs
        // These will be overwritten by the Scheduler every 10 seconds
        saveInitialPrice(CryptoPairEnum.BTCUSDT);
        saveInitialPrice(CryptoPairEnum.ETHUSDT);

        System.out.println("--- Data Initialization Completed Successfully ---");
    }

    /**
     * Helper method to persist wallet information
     */
    private void saveWallet(UserEntity user, CryptoCurrency currency, BigDecimal balance) {
        WalletEntity wallet = new WalletEntity();
        wallet.setUserEntity(user);
        wallet.setCurrency(currency);
        wallet.setBalance(balance);
        walletRepository.save(wallet);
    }

    /**
     * Helper method to bootstrap price records
     */
    private void saveInitialPrice(CryptoPairEnum pair) {
        BestPriceEntity priceEntity = new BestPriceEntity();
        priceEntity.setPair(pair);
        priceEntity.setBestBid(BigDecimal.ZERO);
        priceEntity.setBestAsk(BigDecimal.ZERO);
        bestPriceRepository.save(priceEntity);
    }
}
