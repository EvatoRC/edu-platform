package com.eduplatform;

import com.eduplatform.service.DiscountService;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.*;

class DiscountServiceTest {

    private final DiscountService discountService = new DiscountService();

    @Test
    void validCode_returnsCorrectDiscount() {
        assertThat(discountService.resolveDiscount("BIENVENIDO10", 1))
                .isEqualByComparingTo(BigDecimal.valueOf(10));
    }

    @Test
    void invalidCode_returnsZero() {
        assertThat(discountService.resolveDiscount("FALSO", 1))
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void twoCourses_appliesVolumeDiscount5() {
        assertThat(discountService.resolveDiscount(null, 2))
                .isEqualByComparingTo(BigDecimal.valueOf(5));
    }

    @Test
    void threePlusCourses_appliesVolumeDiscount10() {
        assertThat(discountService.resolveDiscount(null, 3))
                .isEqualByComparingTo(BigDecimal.valueOf(10));
    }

    @Test
    void codeDiscountBeatsVolumeDiscount() {
        assertThat(discountService.resolveDiscount("VERANO20", 3))
                .isEqualByComparingTo(BigDecimal.valueOf(20));
    }

    @Test
    void nullCode_isValid() {
        assertThat(discountService.isValidCode(null)).isTrue();
    }

    @Test
    void unknownCode_isInvalid() {
        assertThat(discountService.isValidCode("NOEXISTE")).isFalse();
    }
}
