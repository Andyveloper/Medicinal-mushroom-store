package com.setas.setas_backend.infrastructure.web.dto;

import com.setas.setas_backend.domain.model.OrderItem;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class OrderItemResponse {
    private final Long id;
    private final Long productId;
    private final String productName;
    private final Integer quantity;
    private final BigDecimal unitPrice;

    public OrderItemResponse(OrderItem item) {
        this.id = item.getId();
        this.productId = item.getProduct().getId();
        this.productName = item.getProduct().getName();
        this.quantity = item.getQuantity();
        this.unitPrice = item.getUnitPrice();
    }
}
