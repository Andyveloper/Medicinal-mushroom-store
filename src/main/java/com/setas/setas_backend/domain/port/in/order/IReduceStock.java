package com.setas.setas_backend.domain.port.in.order;

public interface IReduceStock {
  void execute(Long orderId);
}
