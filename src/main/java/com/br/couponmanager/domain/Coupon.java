package com.br.couponmanager.domain;

import com.br.couponmanager.exception.BusinessRuleException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 6)
    private String code;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Double discountValue;

    @Column(nullable = false)
    private Instant expirationDate;

    @Column(nullable = false)
    private Boolean published;

    @Column(nullable = false)
    private Boolean redeemed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponStatus status;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        if (this.redeemed == null) {
            this.redeemed = false;
        }
        if (this.status == null) {
            this.status = CouponStatus.ACTIVE;
        }
        validateCreationRules();
    }

    private String cleanAndValidateCode(String rawCode) {
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
        Matcher matcher = pattern.matcher(rawCode);
        String cleanedCode = matcher.replaceAll("");

        if (cleanedCode.length() < 6) {
            throw new BusinessRuleException("O código do cupom deve ter pelo menos 6 caracteres alfanuméricos após a remoção de caracteres especiais.");
        }
        return cleanedCode.substring(0, 6).toUpperCase();
    }

    private void validateDiscountValue(Double value) {
        if (value == null || value < 0.5) {
            throw new BusinessRuleException("O valor de desconto deve ser no mínimo 0.5.");
        }
    }

    private void validateExpirationDate(Instant date) {
        if (date == null || date.isBefore(Instant.now())) {
            throw new BusinessRuleException("A data de expiração não pode ser no passado.");
        }
    }

    private void validateCreationRules() {
        this.code = cleanAndValidateCode(this.code);
        validateDiscountValue(this.discountValue);
        validateExpirationDate(this.expirationDate);
    }

    public void softDelete() {
        if (this.status == CouponStatus.DELETED) {
            throw new BusinessRuleException("O cupom já está deletado.");
        }
        this.status = CouponStatus.DELETED;
    }
}
