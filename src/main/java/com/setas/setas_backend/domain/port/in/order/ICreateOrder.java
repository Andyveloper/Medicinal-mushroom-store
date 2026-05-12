package com.setas.setas_backend.domain.port.in.order;

import com.setas.setas_backend.domain.model.Order;

public interface ICreateOrder {
    Order execute(Order order, String email);
}
