package com.aquariux.trading.dtos;

public record BaseErrorResponse(
        String code,
        String message
) {
}