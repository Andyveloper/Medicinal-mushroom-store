package com.setas.setas_backend.domain.port.out;

import com.setas.setas_backend.domain.model.Order;
import com.setas.setas_backend.domain.model.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface IOrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long id);
    List<Order> findByUserId(Long userId);
    Optional<Order> updateStatus(Long orderId, OrderStatus status);
}
