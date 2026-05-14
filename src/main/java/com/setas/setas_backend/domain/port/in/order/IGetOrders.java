package com.setas.setas_backend.domain.port.in.order;

import com.setas.setas_backend.domain.model.Order;

import java.util.List;
import java.util.Optional;

public interface IGetOrders {
  Optional<Order> getById(Long id);

  List<Order> getByUserEmail(String userEmail);
}
