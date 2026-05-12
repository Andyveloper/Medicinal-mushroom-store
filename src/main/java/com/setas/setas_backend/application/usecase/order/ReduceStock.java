package com.setas.setas_backend.application.usecase.order;

import com.setas.setas_backend.domain.model.Order;
import com.setas.setas_backend.domain.model.Product;
import com.setas.setas_backend.domain.port.in.order.IReduceStock;
import com.setas.setas_backend.domain.port.out.IOrderRepository;
import com.setas.setas_backend.domain.port.out.IProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReduceStock implements IReduceStock {

  private final IOrderRepository orderRepository;
  private final IProductRepository productRepository;

  @Override
  @Transactional
  public void execute(Long orderId) {
    Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

    order.getOrderItems().forEach(item -> {
      Product product = item.getProduct();
      int newStock = product.getStock() - item.getQuantity();
      if (newStock < 0) {
        throw new RuntimeException("Not enough stock for product: " + product.getName());
      }
      product.setStock(newStock);
      productRepository.save(product);
    });
  }
}
