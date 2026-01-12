package com.aquariux.trading.controllers;

import com.aquariux.trading.entities.BestPriceEntity;
import com.aquariux.trading.repositories.BestPriceEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller providing endpoints to fetch best price information.
 */
@RestController
@RequestMapping("/api/prices")
@RequiredArgsConstructor
public class PriceController {

    private final BestPriceEntityRepository repository;

    @GetMapping("/latest")
    public ResponseEntity<List<BestPriceEntity>> getLatestPrices() {
        return ResponseEntity.ok(repository.findAll());
    }
}