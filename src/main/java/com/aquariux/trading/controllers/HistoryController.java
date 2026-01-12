package com.aquariux.trading.controllers;

import com.aquariux.trading.entities.TradeHistoryEntity;
import com.aquariux.trading.repositories.TradeHistoryEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller exposing trade history endpoints.
 * <p>Provides retrieval of a user's trade history ordered by execution time.</p>
 */
@RestController
@RequestMapping("/api/histories")
@RequiredArgsConstructor
public class HistoryController {
    private final TradeHistoryEntityRepository historyRepo;

    @GetMapping("/{email}")
    public ResponseEntity<List<TradeHistoryEntity>> getHistory(@PathVariable String email) {
        return ResponseEntity.ok(historyRepo.findByUserEntityEmailOrderByExecutedAtDesc(email));
    }
}