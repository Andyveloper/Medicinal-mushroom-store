package com.setas.setas_backend.application.usecase.order;

import com.setas.setas_backend.domain.model.Order;
import com.setas.setas_backend.domain.port.in.order.IGetOrders;
import com.setas.setas_backend.domain.port.out.IOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetOrders implements IGetOrders {

  private final IOrderRepository orderRepository;

  @Override
  @Transactional
  public Optional<Order> getById(Long id) {
    return orderRepository.findById(id);
  }

  @Override
  @Transactional
  public List<Order> getByUserEmail(String userEmail) {
    return orderRepository.findByUserEmail(userEmail);
  }
}
