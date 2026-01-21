package com.br.couponmanager.domain;

import com.br.couponmanager.exception.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class CouponTest {

    private Coupon validCoupon;

    @BeforeEach
    void setUp() {
        validCoupon = Coupon.builder()
                .code("ABC-123")
                .description("Test Coupon")
                .discountValue(0.8)
                .expirationDate(Instant.now().plus(1, ChronoUnit.DAYS))
                .published(true)
                .build();
    }

    @Test
    void prePersist_ShouldApplyRulesAndSetDefaults() {
        validCoupon.prePersist();

        assertNotNull(validCoupon.getId());
        assertEquals("ABC123", validCoupon.getCode());
        assertEquals(CouponStatus.ACTIVE, validCoupon.getStatus());
        assertFalse(validCoupon.getRedeemed());
    }

    @Test
    void prePersist_ShouldThrowException_WhenCodeIsTooShort() {
        validCoupon.setCode("AB-1");
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, validCoupon::prePersist);
        assertTrue(exception.getMessage().contains("pelo menos 6 caracteres"));
    }

    @Test
    void prePersist_ShouldCleanCodeAndTruncateToSixChars() {
        validCoupon.setCode("ABC-123456789");
        validCoupon.prePersist();
        assertEquals("ABC123", validCoupon.getCode());
    }

    @Test
    void prePersist_ShouldThrowException_WhenDiscountValueIsTooLow() {
        validCoupon.setDiscountValue(0.49);
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, validCoupon::prePersist);
        assertTrue(exception.getMessage().contains("no mínimo 0.5"));
    }

    @Test
    void prePersist_ShouldThrowException_WhenExpirationDateIsInThePast() {
        validCoupon.setExpirationDate(Instant.now().minus(1, ChronoUnit.DAYS));
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, validCoupon::prePersist);
        assertTrue(exception.getMessage().contains("não pode ser no passado"));
    }

    @Test
    void softDelete_ShouldSetStatusToDeleted() {
        validCoupon.setStatus(CouponStatus.ACTIVE);
        validCoupon.softDelete();
        assertEquals(CouponStatus.DELETED, validCoupon.getStatus());
    }

    @Test
    void softDelete_ShouldThrowException_WhenAlreadyDeleted() {
        validCoupon.setStatus(CouponStatus.DELETED);
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, validCoupon::softDelete);
        assertTrue(exception.getMessage().contains("já está deletado"));
    }
}
