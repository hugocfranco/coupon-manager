package com.br.couponmanager.service;

import com.br.couponmanager.domain.Coupon;
import com.br.couponmanager.domain.CouponStatus;
import com.br.couponmanager.dto.CouponRequest;
import com.br.couponmanager.dto.CouponResponse;
import com.br.couponmanager.exception.BusinessRuleException;
import com.br.couponmanager.repository.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Transactional
    public CouponResponse createCoupon(CouponRequest request) {
        Coupon coupon = Coupon.builder()
                .code(request.getCode())
                .description(request.getDescription())
                .discountValue(request.getDiscountValue())
                .expirationDate(request.getExpirationDate())
                .published(request.getPublished())
                .build();

        couponRepository.save(coupon);

        return mapToResponse(coupon);
    }

    public CouponResponse getCoupon(UUID id) {
        return mapToResponse(findActiveCouponById(id));
    }

    @Transactional
    public void deleteCoupon(UUID id) {
        Coupon coupon = findActiveCouponById(id);

        coupon.softDelete();
        couponRepository.save(coupon);
    }

    private Coupon findActiveCouponById(UUID id) {
        return couponRepository.findByIdAndStatusNot(id, CouponStatus.DELETED)
                .orElseThrow(() -> new BusinessRuleException("Cupom não encontrado ou já deletado."));
    }

    private CouponResponse mapToResponse(Coupon coupon) {
        return CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .description(coupon.getDescription())
                .discountValue(coupon.getDiscountValue())
                .expirationDate(coupon.getExpirationDate())
                .published(coupon.getPublished())
                .redeemed(coupon.getRedeemed())
                .status(coupon.getStatus())
                .build();
    }
}
