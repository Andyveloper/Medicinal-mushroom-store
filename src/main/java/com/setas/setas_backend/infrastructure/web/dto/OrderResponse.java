package com.setas.setas_backend.infrastructure.web.dto;

import com.setas.setas_backend.domain.model.Order;
import com.setas.setas_backend.domain.model.OrderStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderResponse {

    private final Long id;
    private final Long userId;
    private final BigDecimal totalPrice;
    private final OrderStatus status;
    private final LocalDateTime createdAt;
    private final List<OrderItemResponse> items;

    public OrderResponse(Order order) {
        this.id = order.getId();
        this.userId = order.getUser().getId();
        this.totalPrice = order.getTotalPrice();
        this.status = order.getStatus();
        this.createdAt = order.getCreatedAt();
        this.items = order.getOrderItems().stream()
                .map(OrderItemResponse::new)
                .toList();
    }
}
