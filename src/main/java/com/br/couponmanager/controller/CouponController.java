package com.br.couponmanager.controller;

import com.br.couponmanager.dto.CouponRequest;
import com.br.couponmanager.dto.CouponResponse;
import com.br.couponmanager.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/coupon")
@Tag(name = "Coupon", description = "API para gerenciamento de cupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping
    @Operation(summary = "Cria um novo cupom", description = "Cadastra um novo cupom aplicando as regras de negócio.")
    public ResponseEntity<CouponResponse> createCoupon(@Valid @RequestBody CouponRequest request) {
        CouponResponse response = couponService.createCoupon(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Recupera cupom", description = "Cadastra um novo cupom aplicando as regras de negócio.")
    public ResponseEntity<CouponResponse> getCoupon(@PathVariable UUID id) {
        CouponResponse response = couponService.getCoupon(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta um cupom", description = "Realiza o delete de um cupom pelo ID.")
    public ResponseEntity<Void> deleteCoupon(@PathVariable UUID id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }
}
