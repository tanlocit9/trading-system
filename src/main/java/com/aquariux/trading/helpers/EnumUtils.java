package com.aquariux.trading.helpers;

import java.util.Optional;

/**
 * Utility helpers for working with enums in a null-safe and case-insensitive manner.
 */
public final class EnumUtils {

    private EnumUtils() {
    }

    /**
     * Compare a string with an enum constant safely (case-insensitive).
     */
    public static <E extends Enum<E>> boolean equals(
            String value,
            E enumConstant
    ) {
        if (value == null || enumConstant == null) {
            return false;
        }

        try {
            return Enum.valueOf(enumConstant.getDeclaringClass(), value.toUpperCase())
                    == enumConstant;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    /**
     * Convert string to enum safely.
     */
    public static <E extends Enum<E>> Optional<E> parse(
            String value,
            Class<E> enumClass
    ) {
        if (value == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(Enum.valueOf(enumClass, value.toUpperCase()));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}
