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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateOrderPaymentIntentTest {

    @Mock
    private IOrderRepository orderRepository;

    @InjectMocks
    private UpdateOrderPaymentIntent updateOrderPaymentIntent;

    @Test
    void execute_deberiaAsignarPaymentIntentIdALaOrden() {
        Order order = Order.builder()
                .id(1L)
                .totalPrice(new BigDecimal("50.00"))
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .orderItems(new ArrayList<>())
                .paymentIntentId("pi_test_123")
                .build();

        when(orderRepository.updatePaymentIntentId(1L, "pi_test_123")).thenReturn(order);

        Order result = updateOrderPaymentIntent.execute(1L, "pi_test_123");

        assertThat(result.getPaymentIntentId()).isEqualTo("pi_test_123");
        verify(orderRepository).updatePaymentIntentId(1L, "pi_test_123");
    }
}
