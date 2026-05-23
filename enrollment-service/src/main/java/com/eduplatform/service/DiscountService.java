package com.eduplatform.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Funcionalidad extra #1: Descuentos por código promocional.
 * Funcionalidad extra #2: Descuento automático por volumen (2+ cursos).
 */
@Service
public class DiscountService {

    private static final Map<String, BigDecimal> DISCOUNT_CODES = Map.of(
            "BIENVENIDO10", BigDecimal.valueOf(10),
            "VERANO20",     BigDecimal.valueOf(20),
            "PROMO15",      BigDecimal.valueOf(15),
            "ESTUDIANTE5",  BigDecimal.valueOf(5)
    );

    public BigDecimal resolveDiscount(String code, int courseCount) {
        BigDecimal codeDiscount = codeDiscount(code);
        BigDecimal volumeDiscount = volumeDiscount(courseCount);
        return codeDiscount.max(volumeDiscount);
    }

    private BigDecimal codeDiscount(String code) {
        if (code == null || code.isBlank()) return BigDecimal.ZERO;
        return DISCOUNT_CODES.getOrDefault(code.toUpperCase(), BigDecimal.ZERO);
    }

    private BigDecimal volumeDiscount(int count) {
        if (count >= 3) return BigDecimal.valueOf(10);
        if (count == 2) return BigDecimal.valueOf(5);
        return BigDecimal.ZERO;
    }

    public boolean isValidCode(String code) {
        if (code == null || code.isBlank()) return true;
        return DISCOUNT_CODES.containsKey(code.toUpperCase());
    }
}
