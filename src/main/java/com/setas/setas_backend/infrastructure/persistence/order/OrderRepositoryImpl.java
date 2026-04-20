package com.setas.setas_backend.infrastructure.persistence.order;

import com.setas.setas_backend.domain.model.Order;
import com.setas.setas_backend.domain.model.OrderStatus;
import com.setas.setas_backend.domain.port.out.IOrderRepository;
import com.setas.setas_backend.infrastructure.web.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderRepositoryImpl implements IOrderRepository {

    private final JpaOrderRepository jpaOrderRepository;

    @Override
    public Order save(Order order) {
        return jpaOrderRepository.save(order);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return jpaOrderRepository.findById(id);
    }

   @Override
   public List<Order> findByUserId(Long userId) {
        return jpaOrderRepository.findByUserId(userId);
   }

   @Override
   @Transactional
    public Optional<Order> updateStatus(Long orderId, OrderStatus status) {
       jpaOrderRepository.updateStatus(orderId, status);
       return jpaOrderRepository.findById(orderId);
   }
}
