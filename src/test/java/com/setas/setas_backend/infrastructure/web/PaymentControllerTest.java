package com.setas.setas_backend.infrastructure.web;

import com.setas.setas_backend.config.StripeService;
import com.setas.setas_backend.domain.model.*;
import com.setas.setas_backend.domain.port.in.order.IGetOrders;
import com.setas.setas_backend.domain.port.in.order.IUpdateOrderPaymentIntent;
import com.setas.setas_backend.domain.port.in.user.IFindByEmailUser;
import com.stripe.exception.StripeException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock private StripeService stripeService;
    @Mock private IGetOrders getOrders;
    @Mock private IFindByEmailUser findByEmailUser;
    @Mock private IUpdateOrderPaymentIntent updateOrderPaymentIntent;

    @InjectMocks
    private PaymentController paymentController;

    private MockMvc mockMvc;

    private final User user = User.builder()
            .id(1L).name("Andy").lastname("Dev")
            .email("andy@test.com").role(Role.CLIENT).build();

    private final User otherUser = User.builder()
            .id(2L).name("Other").lastname("User")
            .email("other@test.com").role(Role.CLIENT).build();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "andy@test.com", null,
                        List.of(new SimpleGrantedAuthority("ROLE_CLIENT"))));
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    private Order buildOrder(User orderUser) {
        return Order.builder()
                .id(1L).user(orderUser)
                .totalPrice(new BigDecimal("50.00"))
                .status(OrderStatus.PENDING)
                .orderItems(new ArrayList<>())
                .build();
    }

    @Test
    void createPayment_shouldReturn200WithClientSecret() throws Exception {
        Order order = buildOrder(user);
        when(findByEmailUser.execute("andy@test.com")).thenReturn(Optional.of(user));
        when(getOrders.getById(1L)).thenReturn(Optional.of(order));
        when(stripeService.createPaymentIntent(any(BigDecimal.class), eq(1L), eq(user)))
                .thenReturn("pi_abc123_secret_xyz456");

        mockMvc.perform(post("/api/payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientSecret").value("pi_abc123_secret_xyz456"))
                .andExpect(jsonPath("$.orderId").value(1));
    }

    @Test
    void createPayment_shouldReturn403WhenOrderBelongsToAnotherUser() throws Exception {
        Order order = buildOrder(otherUser);
        when(findByEmailUser.execute("andy@test.com")).thenReturn(Optional.of(user));
        when(getOrders.getById(1L)).thenReturn(Optional.of(order));

        mockMvc.perform(post("/api/payments/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void createPayment_shouldReturn500WhenStripeThrowsException() throws Exception {
        Order order = buildOrder(user);
        when(findByEmailUser.execute("andy@test.com")).thenReturn(Optional.of(user));
        when(getOrders.getById(1L)).thenReturn(Optional.of(order));
        when(stripeService.createPaymentIntent(any(), any(), any()))
                .thenThrow(mock(StripeException.class));

        mockMvc.perform(post("/api/payments/1"))
                .andExpect(status().isInternalServerError());
    }
}
