package com.setas.setas_backend.application.usecase.order;

import com.setas.setas_backend.domain.model.*;
import com.setas.setas_backend.domain.port.out.IOrderRepository;
import com.setas.setas_backend.domain.port.out.IProductRepository;
import com.setas.setas_backend.domain.port.out.IUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateOrderTest {

    @Mock private IOrderRepository orderRepository;
    @Mock private IProductRepository productRepository;
    @Mock private IUserRepository userRepository;

    @InjectMocks
    private CreateOrder createOrder;

    private final User user = User.builder().id(1L).name("Andy").lastname("Dev").email("andy@test.com").role(Role.CLIENT).build();

    private final Product reishi = Product.builder().id(1L).name("Reishi").price(new BigDecimal("25.00")).stock(10).active(true).build();

    @Test
    void execute_deberiaCrearOrdenConTotalCalculadoYEstadoPending() {
        OrderItem item = new OrderItem();
        item.setProduct(Product.builder().id(1L).build());
        item.setQuantity(2);

        List<OrderItem> items = new ArrayList<>(List.of(item));
        Order order = new Order();
        order.setOrderItems(items);

        Order saved = Order.builder()
                .id(1L)
                .user(user)
                .totalPrice(new BigDecimal("50.00"))
                .status(OrderStatus.PENDING)
                .orderItems(items)
                .build();

        when(userRepository.findByEmail("andy@test.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(reishi));
        when(orderRepository.save(any(Order.class))).thenReturn(saved);

        Order result = createOrder.execute(order, "andy@test.com");

        assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(result.getTotalPrice()).isEqualByComparingTo("50.00");
        assertThat(result.getUser()).isEqualTo(user);
    }

    @Test
    void execute_deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        Order order = new Order();
        order.setOrderItems(new ArrayList<>());

        when(userRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> createOrder.execute(order, "noexiste@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void execute_deberiaLanzarExcepcionCuandoProductoNoExiste() {
        OrderItem item = new OrderItem();
        item.setProduct(Product.builder().id(99L).build());
        item.setQuantity(1);

        Order order = new Order();
        order.setOrderItems(new ArrayList<>(List.of(item)));

        when(userRepository.findByEmail("andy@test.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> createOrder.execute(order, "andy@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product not found");
    }
}
