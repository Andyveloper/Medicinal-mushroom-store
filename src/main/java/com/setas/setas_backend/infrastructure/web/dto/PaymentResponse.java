package com.setas.setas_backend.infrastructure.web.dto;

public record PaymentResponse(String clientSecret, Long orderId) {}
