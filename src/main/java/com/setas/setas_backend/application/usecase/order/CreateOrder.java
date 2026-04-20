package com.setas.setas_backend.application.usecase.order;


import com.setas.setas_backend.domain.model.Order;
import com.setas.setas_backend.domain.model.OrderStatus;
import com.setas.setas_backend.domain.model.Product;
import com.setas.setas_backend.domain.port.in.order.ICreateOrder;
import com.setas.setas_backend.domain.port.out.IOrderRepository;
import com.setas.setas_backend.domain.port.out.IProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateOrder implements ICreateOrder {

    private final IOrderRepository orderRepository;
    private final IProductRepository productRepository;

    @Override
    public Order execute(Order order) {

        order.getOrderItems().forEach(item -> {
            Product product = productRepository.findById(item.getProduct().getId()).orElseThrow(() -> new RuntimeException("Product not found" + item.getProduct().getId()));
            item.setProduct(product);
        });

        BigDecimal totalPrice = order.getOrderItems().stream().map(item -> item.getUnitPrice()
                .multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }
}
