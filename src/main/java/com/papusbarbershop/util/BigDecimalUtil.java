package com.papusbarbershop.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utilidades para evitar NPE con montos leidos de BD o APIs (valores null).
 */
public final class BigDecimalUtil {

    private BigDecimalUtil() {
    }

    public static BigDecimal nvl(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    public static BigDecimal nvl(BigDecimal value, BigDecimal whenNull) {
        return value != null ? value : (whenNull != null ? whenNull : BigDecimal.ZERO);
    }

    /** Escala tipica para dinero (2 decimales). */
    public static BigDecimal scaleMoney(BigDecimal value) {
        return nvl(value).setScale(2, RoundingMode.HALF_UP);
    }

    public static int nvlInt(Integer value) {
        return value != null ? value : 0;
    }
}
