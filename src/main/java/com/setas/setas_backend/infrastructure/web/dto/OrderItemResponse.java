package com.setas.setas_backend.infrastructure.web.dto;

import com.setas.setas_backend.domain.model.OrderItem;

import java.math.BigDecimal;

public record OrderItemResponse(Long id, Long productId, String productName, Integer quantity, BigDecimal unitPrice) {

  public OrderItemResponse(OrderItem item) {
    this(
            item.getId(),
            item.getProduct().getId(),
            item.getProduct().getName(),
            item.getQuantity(),
            item.getUnitPrice()
    );
  }
}