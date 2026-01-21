package com.br.couponmanager.dto;

import com.br.couponmanager.domain.CouponStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class CouponResponse {
    private UUID id;
    private String code;
    private String description;
    private Double discountValue;
    private Instant expirationDate;
    private Boolean published;
    private Boolean redeemed;
    private CouponStatus status;
}
