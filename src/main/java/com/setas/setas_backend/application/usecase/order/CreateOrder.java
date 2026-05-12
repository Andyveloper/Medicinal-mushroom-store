package com.setas.setas_backend.application.usecase.order;


import com.setas.setas_backend.domain.model.Order;
import com.setas.setas_backend.domain.model.OrderStatus;
import com.setas.setas_backend.domain.model.Product;
import com.setas.setas_backend.domain.model.User;
import com.setas.setas_backend.domain.port.in.order.ICreateOrder;
import com.setas.setas_backend.domain.port.out.IOrderRepository;
import com.setas.setas_backend.domain.port.out.IProductRepository;
import com.setas.setas_backend.domain.port.out.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateOrder implements ICreateOrder {

    private final IOrderRepository orderRepository;
    private final IProductRepository productRepository;
    private final IUserRepository userRepository;

    @Transactional
    @Override
    public Order execute(Order order, String email) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        order.setUser(user);

        order.getOrderItems().forEach(item -> {
            Product product = productRepository.findById(item.getProduct().getId()).orElseThrow(() -> new RuntimeException("Product not found" + item.getProduct().getId()));
            item.setProduct(product);
            item.setUnitPrice(product.getPrice());
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
