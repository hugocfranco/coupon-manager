package com.br.couponmanager.repository;

import com.br.couponmanager.domain.CouponStatus;
import com.br.couponmanager.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {
    Optional<Coupon> findByIdAndStatusNot(UUID id, CouponStatus status);
}
