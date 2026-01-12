package com.aquariux.trading.controllers;

import com.aquariux.trading.dtos.TradeRequest;
import com.aquariux.trading.entities.TradeHistoryEntity;
import com.aquariux.trading.enums.CryptoPairEnum;
import com.aquariux.trading.enums.OrderSideEnum;
import com.aquariux.trading.services.TradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that accepts trade execution requests and delegates to `TradeService`.
 */
@RestController
@RequestMapping("/api/trade")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @PostMapping("/execute")
    public ResponseEntity<TradeHistoryEntity> executeTrade(@Valid @RequestBody TradeRequest request) {
        TradeHistoryEntity result = tradeService.executeTrade(
                request.getEmail(),
                CryptoPairEnum.valueOf(request.getPair().toUpperCase()),
                OrderSideEnum.valueOf(request.getSide().toUpperCase()),
                request.getQuantity()
        );
        return ResponseEntity.ok(result);
    }
}
