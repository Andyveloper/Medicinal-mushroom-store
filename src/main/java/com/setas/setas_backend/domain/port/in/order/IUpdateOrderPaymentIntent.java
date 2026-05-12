package com.setas.setas_backend.domain.port.in.order;

import com.setas.setas_backend.domain.model.Order;

public interface IUpdateOrderPaymentIntent {
    Order execute(Long orderId, String paymentIntentId);
}
