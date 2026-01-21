package com.br.couponmanager.service;

import com.br.couponmanager.domain.Coupon;
import com.br.couponmanager.domain.CouponStatus;
import com.br.couponmanager.dto.CouponRequest;
import com.br.couponmanager.dto.CouponResponse;
import com.br.couponmanager.exception.BusinessRuleException;
import com.br.couponmanager.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    private CouponRequest validRequest;
    private Coupon validCoupon;
    private UUID couponId;

    @BeforeEach
    void setUp() {
        couponId = UUID.randomUUID();
        validRequest = new CouponRequest();
        validRequest.setCode("ABC-123");
        validRequest.setDescription("Test Coupon");
        validRequest.setDiscountValue(0.8);
        validRequest.setExpirationDate(Instant.now().plus(1, ChronoUnit.DAYS));
        validRequest.setPublished(true);

        validCoupon = Coupon.builder()
                .id(couponId)
                .code("ABC123")
                .description("Test Coupon")
                .discountValue(0.8)
                .expirationDate(validRequest.getExpirationDate())
                .published(true)
                .redeemed(false)
                .status(CouponStatus.ACTIVE)
                .build();
    }

    @Test
    void createCoupon_ShouldReturnCouponResponse_WhenRequestIsValid() {
        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> {
            Coupon couponToSave = invocation.getArgument(0);
            // Simula o comportamento do @PrePersist que limpa o código
            couponToSave.setCode("ABC123");
            return couponToSave;
        });

        CouponResponse response = couponService.createCoupon(validRequest);

        assertNotNull(response);
        assertEquals("ABC123", response.getCode());
        verify(couponRepository, times(1)).save(any(Coupon.class));
    }

    @Test
    void createCoupon_ShouldThrowException_WhenBusinessRuleIsViolated() {
        validRequest.setDiscountValue(0.4); // Violates rule: min 0.5

        // Mock para simular que a exceção é lançada durante o save (prePersist)
        doThrow(new BusinessRuleException("O valor de desconto deve ser no mínimo 0.5."))
                .when(couponRepository).save(any(Coupon.class));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class,
                () -> couponService.createCoupon(validRequest));

        assertTrue(exception.getMessage().contains("no mínimo 0.5"));
    }

    @Test
    void getCoupon_ShouldReturnCouponResponse_WhenCouponExistsAndIsActive() {
        // Arrange
        when(couponRepository.findByIdAndStatusNot(couponId, CouponStatus.DELETED))
                .thenReturn(Optional.of(validCoupon));

        // Act
        CouponResponse response = couponService.getCoupon(couponId);

        // Assert
        assertNotNull(response);
        assertEquals(couponId, response.getId());
        assertEquals(CouponStatus.ACTIVE, response.getStatus());
    }

    @Test
    void getCoupon_ShouldThrowException_WhenCouponIsNotFound() {
        // Arrange
        when(couponRepository.findByIdAndStatusNot(couponId, CouponStatus.DELETED))
                .thenReturn(Optional.empty());

        // Act & Assert
        BusinessRuleException exception = assertThrows(BusinessRuleException.class,
                () -> couponService.getCoupon(couponId));

        assertTrue(exception.getMessage().contains("não encontrado ou já deletado"));
    }

    @Test
    void deleteCoupon_ShouldPerformSoftDelete_WhenCouponExistsAndIsNotDeleted() {
        when(couponRepository.findByIdAndStatusNot(couponId, CouponStatus.DELETED))
                .thenReturn(Optional.of(validCoupon));

        couponService.deleteCoupon(couponId);

        assertEquals(CouponStatus.DELETED, validCoupon.getStatus());
        verify(couponRepository, times(1)).save(validCoupon);
    }

    @Test
    void deleteCoupon_ShouldThrowException_WhenCouponIsNotFound() {
        when(couponRepository.findByIdAndStatusNot(couponId, CouponStatus.DELETED))
                .thenReturn(Optional.empty());

        BusinessRuleException exception = assertThrows(BusinessRuleException.class,
                () -> couponService.deleteCoupon(couponId));

        assertTrue(exception.getMessage().contains("não encontrado ou já deletado"));
        verify(couponRepository, never()).save(any(Coupon.class));
    }

    @Test
    void deleteCoupon_ShouldThrowException_WhenCouponIsAlreadyDeleted() {
        Coupon deletedCoupon = Coupon.builder().status(CouponStatus.DELETED).build();
        when(couponRepository.findByIdAndStatusNot(couponId, CouponStatus.DELETED))
                .thenReturn(Optional.empty()); // findByIdAndStatusNot deve retornar vazio se o status for DELETED

        BusinessRuleException exception = assertThrows(BusinessRuleException.class,
                () -> couponService.deleteCoupon(couponId));

        assertTrue(exception.getMessage().contains("já deletado"));
        verify(couponRepository, never()).save(any(Coupon.class));
    }
}
