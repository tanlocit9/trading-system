package com.aquariux.trading.dtos;

/**
 * Simple immutable error response used by REST controllers to return error codes and messages.
 *
 * @param code    short error code identifying the error type
 * @param message human-readable error description
 */
public record BaseErrorResponse(
        String code,
        String message
) {
}