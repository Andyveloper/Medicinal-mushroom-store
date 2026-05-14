package com.setas.setas_backend.application.usecase.order;

import com.setas.setas_backend.domain.model.Order;
import com.setas.setas_backend.domain.model.OrderStatus;
import com.setas.setas_backend.domain.port.out.IOrderRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetOrdersTest {

    @Mock
    private IOrderRepository orderRepository;

    @InjectMocks
    private GetOrders getOrders;

    private final Order order = Order.builder()
            .id(1L)
            .totalPrice(new BigDecimal("50.00"))
            .status(OrderStatus.PENDING)
            .createdAt(LocalDateTime.now())
            .orderItems(new ArrayList<>())
            .build();

    @Test
    void getById_deberiaRetornarOrdenCuandoExiste() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Optional<Order> result = getOrders.getById(1L);

        assertThat(result).isPresent().contains(order);
    }

    @Test
    void getById_deberiaRetornarVacioCuandoNoExiste() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Order> result = getOrders.getById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void getByUserEmail_deberiaRetornarOrdenesDelUsuario() {
        when(orderRepository.findByUserEmail("andy@test.com")).thenReturn(List.of(order));

        List<Order> result = getOrders.getByUserEmail("andy@test.com");

        assertThat(result).hasSize(1).containsExactly(order);
    }

    @Test
    void getAll_deberiaRetornarTodasLasOrdenes() {
        Order order2 = Order.builder().id(2L).totalPrice(new BigDecimal("100.00")).status(OrderStatus.PAID).createdAt(LocalDateTime.now()).orderItems(new ArrayList<>()).build();
        when(orderRepository.getAll()).thenReturn(List.of(order, order2));

        List<Order> result = getOrders.getAll();

        assertThat(result).hasSize(2);
    }
}
