package com.setas.setas_backend.infrastructure.web.dto;

import com.setas.setas_backend.domain.model.Order;
import com.setas.setas_backend.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(Long id, Long userId, String userEmail, String userName, Long userPhone,
                            BigDecimal totalPrice,
                            OrderStatus status, LocalDateTime createdAt,
                            List<OrderItemResponse> items) {
  public OrderResponse(Order order) {
    this(
            order.getId(),
            order.getUser().getId(),
            order.getUser().getEmail(),
            order.getUser().getName() + " " + order.getUser().getLastname(),
            order.getUser().getPhoneNumber(),
            order.getTotalPrice(),
            order.getStatus(),
            order.getCreatedAt(),
            order.getOrderItems().stream()
                    .map(OrderItemResponse::new)
                    .toList()
    );
  }

}