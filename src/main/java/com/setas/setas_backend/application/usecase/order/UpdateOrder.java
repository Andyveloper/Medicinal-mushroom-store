package com.setas.setas_backend.application.usecase.order;

import com.setas.setas_backend.domain.model.Order;
import com.setas.setas_backend.domain.model.OrderStatus;
import com.setas.setas_backend.domain.port.in.order.IUpdateOrderStatus;
import com.setas.setas_backend.domain.port.out.IOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpdateOrder implements IUpdateOrderStatus {

    private final IOrderRepository orderRepository;

    @Override
    public Optional<Order> execute(Long orderId, OrderStatus status) {
        Optional<Order> created = orderRepository.findById(orderId);

        if (created.isPresent() && created.get().getStatus().equals(OrderStatus.PENDING)) {
            return orderRepository.updateStatus(orderId, status);
        } else {
            return Optional.empty();
        }
    }
}
