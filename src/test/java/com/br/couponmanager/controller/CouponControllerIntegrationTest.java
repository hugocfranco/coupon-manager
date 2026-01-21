package com.br.couponmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.br.couponmanager.domain.Coupon;
import com.br.couponmanager.domain.CouponStatus;
import com.br.couponmanager.dto.CouponRequest;
import com.br.couponmanager.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CouponControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CouponRepository couponRepository;

    @BeforeEach
    void setUp() {
        couponRepository.deleteAll();
    }

    @Test
    void createCoupon_ShouldReturnCreatedAndCouponResponse_WhenRequestIsValid() throws Exception {
        CouponRequest request = new CouponRequest();
        request.setCode("ABC-123");
        request.setDescription("Test Coupon");
        request.setDiscountValue(0.8);
        request.setExpirationDate(Instant.now().plus(1, ChronoUnit.DAYS));
        request.setPublished(true);

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code", is("ABC123")))
                .andExpect(jsonPath("$.discountValue", is(0.8)))
                .andExpect(jsonPath("$.status", is("ACTIVE")));
    }

    @Test
    void createCoupon_ShouldReturnBadRequest_WhenDiscountValueIsTooLow() throws Exception {
        CouponRequest request = new CouponRequest();
        request.setCode("ABC-123");
        request.setDescription("Test Coupon");
        request.setDiscountValue(0.4); // Invalid value
        request.setExpirationDate(Instant.now().plus(1, ChronoUnit.DAYS));
        request.setPublished(true);

        MvcResult result = mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("O valor de desconto deve ser no mínimo 0.5."));
    }

    @Test
    void createCoupon_ShouldReturnBadRequest_WhenExpirationDateIsInThePast() throws Exception {
        CouponRequest request = new CouponRequest();
        request.setCode("ABC-123");
        request.setDescription("Test Coupon");
        request.setDiscountValue(0.8);
        request.setExpirationDate(Instant.now().minus(1, ChronoUnit.DAYS)); // Invalid date
        request.setPublished(true);

        MvcResult result = mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("A data de expiração não pode ser no passado."));
    }

    @Test
    void getCoupon_ShouldReturnOkAndCouponResponse_WhenCouponExistsAndIsActive() throws Exception {
        // Arrange
        Coupon coupon = Coupon.builder()
                .code("ABC123")
                .description("Test Coupon")
                .discountValue(0.8)
                .expirationDate(Instant.now().plus(1, ChronoUnit.DAYS))
                .published(true)
                .redeemed(false)
                .status(CouponStatus.ACTIVE)
                .build();
        couponRepository.save(coupon);

        // Act & Assert
        mockMvc.perform(get("/coupon/{id}", coupon.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(coupon.getId().toString())))
                .andExpect(jsonPath("$.code", is("ABC123")))
                .andExpect(jsonPath("$.status", is("ACTIVE")));
    }

    @Test
    void getCoupon_ShouldReturnBadRequest_WhenCouponIsDeleted() throws Exception {
        // Arrange
        Coupon coupon = Coupon.builder()
                .code("ABC123")
                .description("Test Coupon")
                .discountValue(0.8)
                .expirationDate(Instant.now().plus(1, ChronoUnit.DAYS))
                .published(true)
                .redeemed(false)
                .status(CouponStatus.DELETED)
                .build();
        couponRepository.save(coupon);

        // Act & Assert
        mockMvc.perform(get("/coupon/{id}", coupon.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteCoupon_ShouldReturnNoContent_WhenCouponExists() throws Exception {
        Coupon coupon = Coupon.builder()
                .code("ABC123")
                .description("Test Coupon")
                .discountValue(0.8)
                .expirationDate(Instant.now().plus(1, ChronoUnit.DAYS))
                .published(true)
                .redeemed(false)
                .status(CouponStatus.ACTIVE)
                .build();
        couponRepository.save(coupon);

        mockMvc.perform(delete("/coupon/{id}", coupon.getId()))
                .andExpect(status().isNoContent());

        Coupon deletedCoupon = couponRepository.findById(coupon.getId()).orElseThrow();
        assertEquals(CouponStatus.DELETED, deletedCoupon.getStatus());
    }


    @Test
    void deleteCoupon_ShouldReturnBadRequest_WhenCouponIsAlreadyDeleted() throws Exception {
        Coupon coupon = Coupon.builder()
                .code("ABC123")
                .description("Test Coupon")
                .discountValue(0.8)
                .expirationDate(Instant.now().plus(1, ChronoUnit.DAYS))
                .published(true)
                .redeemed(false)
                .status(CouponStatus.DELETED)
                .build();
        couponRepository.save(coupon);

        mockMvc.perform(delete("/coupon/{id}", coupon.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteCoupon_ShouldReturnBadRequest_WhenCouponDoesNotExist() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        mockMvc.perform(delete("/coupon/{id}", nonExistentId))
                .andExpect(status().isBadRequest());
    }
}
