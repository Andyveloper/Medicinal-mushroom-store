package com.setas.setas_backend.domain.port.in.order;

import com.setas.setas_backend.domain.model.Order;
import com.setas.setas_backend.domain.model.OrderStatus;

import java.util.Optional;

public interface IUpdateOrderStatus {
    Optional<Order> execute(Long orderId, OrderStatus status);
}
