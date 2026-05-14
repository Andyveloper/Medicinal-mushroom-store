package com.setas.setas_backend.application.usecase.order;

import com.setas.setas_backend.domain.model.*;
import com.setas.setas_backend.domain.port.out.IOrderRepository;
import com.setas.setas_backend.domain.port.out.IProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReduceStockTest {

    @Mock private IOrderRepository orderRepository;
    @Mock private IProductRepository productRepository;

    @InjectMocks
    private ReduceStock reduceStock;

    private Order buildOrderWithItem(Product product, int quantity) {
        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(quantity);

        Order order = Order.builder()
                .id(1L)
                .totalPrice(new BigDecimal("50.00"))
                .status(OrderStatus.PAID)
                .createdAt(LocalDateTime.now())
                .orderItems(new ArrayList<>(List.of(item)))
                .build();
        item.setOrder(order);
        return order;
    }

    @Test
    void execute_deberiaReducirStockCorrectamente() {
        Product reishi = Product.builder().id(1L).name("Reishi").price(new BigDecimal("25.00")).stock(10).active(true).build();
        Order order = buildOrderWithItem(reishi, 3);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.save(any(Product.class))).thenReturn(reishi);

        reduceStock.execute(1L);

        assertThat(reishi.getStock()).isEqualTo(7);
        verify(productRepository).save(reishi);
    }

    @Test
    void execute_deberiaLanzarExcepcionCuandoOrdenNoExiste() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reduceStock.execute(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Order not found");
    }

    @Test
    void execute_deberiaLanzarExcepcionCuandoStockInsuficiente() {
        Product reishi = Product.builder().id(1L).name("Reishi").price(new BigDecimal("25.00")).stock(2).active(true).build();
        Order order = buildOrderWithItem(reishi, 5);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> reduceStock.execute(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Not enough stock");
    }
}
