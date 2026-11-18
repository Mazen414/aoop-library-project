package com.library.util;

import com.library.exception.InvalidLibraryArgumentException;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Shared validation and monetary normalization utilities for the library domain.
 * All monetary amounts are canonically scaled to 2 decimal places using {@link RoundingMode#HALF_UP}.
 */
public final class Validation {

    public static final int MONEY_SCALE = 2;
    public static final BigDecimal ZERO_MONEY = BigDecimal.ZERO.setScale(MONEY_SCALE, RoundingMode.HALF_UP);

    private Validation() {
        // Utility class
    }

    public static <T> T requireNonNull(T value, String fieldName) {
        if (value == null) {
            throw new InvalidLibraryArgumentException(fieldName + " must not be null.");
        }
        return value;
    }

    public static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidLibraryArgumentException(fieldName + " must not be null or blank.");
        }
        return value.trim();
    }

    public static int requirePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new InvalidLibraryArgumentException(fieldName + " must be positive, but was: " + value);
        }
        return value;
    }

    public static long requirePositive(long value, String fieldName) {
        if (value <= 0L) {
            throw new InvalidLibraryArgumentException(fieldName + " must be positive, but was: " + value);
        }
        return value;
    }

    public static long requireNonNegative(long value, String fieldName) {
        if (value < 0L) {
            throw new InvalidLibraryArgumentException(fieldName + " must not be negative, but was: " + value);
        }
        return value;
    }

    public static BigDecimal normalizeMoney(BigDecimal value, String fieldName) {
        requireNonNull(value, fieldName);
        return value.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal requireNonNegativeMoney(BigDecimal value, String fieldName) {
        requireNonNull(value, fieldName);
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidLibraryArgumentException(fieldName + " must not be negative, but was: " + value);
        }
        return value.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}
