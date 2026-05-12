package com.setas.setas_backend.application.usecase.order;

import com.setas.setas_backend.domain.model.Order;
import com.setas.setas_backend.domain.port.in.order.IUpdateOrderPaymentIntent;
import com.setas.setas_backend.domain.port.out.IOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateOrderPaymentIntent implements IUpdateOrderPaymentIntent {
    private final IOrderRepository orderRepository;

    @Override
    @Transactional
    public Order execute(Long orderId, String paymentIntentId) {
        return orderRepository.updatePaymentIntentId(orderId, paymentIntentId);
    }
}
