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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateOrderTest {

    @Mock
    private IOrderRepository orderRepository;

    @InjectMocks
    private UpdateOrder updateOrder;

    private Order buildOrder(OrderStatus status) {
        return Order.builder()
                .id(1L)
                .totalPrice(new BigDecimal("50.00"))
                .status(status)
                .createdAt(LocalDateTime.now())
                .orderItems(new ArrayList<>())
                .build();
    }

    @Test
    void execute_deberiaActualizarEstadoCuandoOrdenEstaPending() {
        Order pending = buildOrder(OrderStatus.PENDING);
        Order paid    = buildOrder(OrderStatus.PAID);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(pending));
        when(orderRepository.updateStatus(1L, OrderStatus.PAID)).thenReturn(Optional.of(paid));

        Optional<Order> result = updateOrder.execute(1L, OrderStatus.PAID);

        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo(OrderStatus.PAID);
        verify(orderRepository).updateStatus(1L, OrderStatus.PAID);
    }

    @Test
    void execute_deberiaRetornarVacioCuandoOrdenNoEstaPending() {
        Order paid = buildOrder(OrderStatus.PAID);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(paid));

        Optional<Order> result = updateOrder.execute(1L, OrderStatus.CANCELLED);

        assertThat(result).isEmpty();
        verify(orderRepository, never()).updateStatus(any(), any());
    }

    @Test
    void execute_deberiaRetornarVacioCuandoOrdenNoExiste() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Order> result = updateOrder.execute(99L, OrderStatus.PAID);

        assertThat(result).isEmpty();
        verify(orderRepository, never()).updateStatus(any(), any());
    }
}
