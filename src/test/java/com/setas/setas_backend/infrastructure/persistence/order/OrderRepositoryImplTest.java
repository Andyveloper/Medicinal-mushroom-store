package com.setas.setas_backend.infrastructure.persistence.order;

import com.setas.setas_backend.domain.model.*;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryImplTest {

    @Mock private JpaOrderRepository jpaOrderRepository;

    @InjectMocks
    private OrderRepositoryImpl orderRepositoryImpl;

    private final User user = User.builder().id(1L).name("Andy").lastname("Dev").email("andy@test.com").role(Role.CLIENT).build();

    private Order buildOrder(Long id) {
        return Order.builder()
                .id(id).user(user)
                .totalPrice(new BigDecimal("50.00"))
                .status(OrderStatus.PENDING)
                .orderItems(new ArrayList<>())
                .build();
    }

    @Test
    void save_shouldDelegateToJpaRepository() {
        Order order = buildOrder(1L);
        when(jpaOrderRepository.save(order)).thenReturn(order);

        Order result = orderRepositoryImpl.save(order);

        assertThat(result).isEqualTo(order);
        verify(jpaOrderRepository).save(order);
    }

    @Test
    void findById_shouldReturnOrderWhenExists() {
        Order order = buildOrder(1L);
        when(jpaOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        Optional<Order> result = orderRepositoryImpl.findById(1L);

        assertThat(result).isPresent().contains(order);
    }

    @Test
    void getAll_shouldReturnAllOrders() {
        Order order = buildOrder(1L);
        when(jpaOrderRepository.findAll()).thenReturn(List.of(order));

        List<Order> result = orderRepositoryImpl.getAll();

        assertThat(result).containsExactly(order);
    }

    @Test
    void findByUserEmail_shouldReturnOrdersForUser() {
        Order order = buildOrder(1L);
        when(jpaOrderRepository.findByUserEmail("andy@test.com")).thenReturn(List.of(order));

        List<Order> result = orderRepositoryImpl.findByUserEmail("andy@test.com");

        assertThat(result).containsExactly(order);
    }

    @Test
    void updateStatus_shouldUpdateAndReturnOrder() {
        Order paid = buildOrder(1L);
        paid.setStatus(OrderStatus.PAID);
        when(jpaOrderRepository.findById(1L)).thenReturn(Optional.of(paid));

        Optional<Order> result = orderRepositoryImpl.updateStatus(1L, OrderStatus.PAID);

        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo(OrderStatus.PAID);
        verify(jpaOrderRepository).updateStatus(1L, OrderStatus.PAID);
    }

    @Test
    void updatePaymentIntentId_shouldReturnUpdatedOrder() {
        Order order = buildOrder(1L);
        when(jpaOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order result = orderRepositoryImpl.updatePaymentIntentId(1L, "pi_abc123");

        assertThat(result).isEqualTo(order);
        verify(jpaOrderRepository).updatePaymentIntentId(1L, "pi_abc123");
    }

    @Test
    void updatePaymentIntentId_shouldThrowWhenOrderNotFound() {
        when(jpaOrderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderRepositoryImpl.updatePaymentIntentId(99L, "pi_xyz"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }
}
