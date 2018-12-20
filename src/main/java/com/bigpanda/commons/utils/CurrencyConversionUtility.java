package com.bigpanda.commons.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyConversionUtility {
    public static final int numOfDigitsAfterDot = 2;

    public static long convertUsdToCed(Long priceInUSD, double currencyRate) {
        return (long) Math.ceil(priceInUSD / currencyRate);
    }

    public static long convertCedToUsd(Long priceInCed, double currencyRate) {
        return (long) Math.ceil(priceInCed * currencyRate);
    }

    public static long getAmountAsLong(double amount) {
        return (long) Math.ceil(amount * precisionConvertion());
    }

    public static double getAmountAsDouble(long amount) {
        return (double) amount / precisionConvertion();
    }

    public static BigDecimal getAmountAsBigDecimal(Long amount) {
        return new BigDecimal(amount).divide(new BigDecimal(precisionConvertion()), numOfDigitsAfterDot, RoundingMode.UP);
    }

    private static double precisionConvertion() {
        return Math.pow(10, numOfDigitsAfterDot);
    }
}
