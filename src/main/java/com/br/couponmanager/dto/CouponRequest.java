package com.br.couponmanager.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class CouponRequest {

    @NotBlank(message = "O código é obrigatório.")
    private String code;

    @NotBlank(message = "A descrição é obrigatória.")
    private String description;

    @NotNull(message = "O valor de desconto é obrigatório.")
    @Min(value = 0, message = "O valor de desconto deve ser no mínimo 0.5.") // A validação de 0.5 será feita na entidade/serviço
    private Double discountValue;

    @NotNull(message = "A data de expiração é obrigatória.")
    private Instant expirationDate;

    private Boolean published = false;
}
