package com.setas.setas_backend.infrastructure.web;

import com.setas.setas_backend.domain.model.*;
import com.setas.setas_backend.domain.port.in.order.ICreateOrder;
import com.setas.setas_backend.domain.port.in.order.IGetOrders;
import com.setas.setas_backend.domain.port.in.order.IUpdateOrderStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock private ICreateOrder createOrder;
    @Mock private IGetOrders getOrders;
    @Mock private IUpdateOrderStatus updateOrderStatus;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;

    private final User user = User.builder()
            .id(1L).name("Andy").lastname("Dev")
            .email("andy@test.com").phoneNumber(123456789L).role(Role.CLIENT).build();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "andy@test.com", null,
                        List.of(new SimpleGrantedAuthority("ROLE_CLIENT"))));
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    private Order buildOrder(Long id, OrderStatus status) {
        return Order.builder()
                .id(id)
                .user(user)
                .totalPrice(new BigDecimal("50.00"))
                .status(status)
                .createdAt(null)
                .orderItems(new ArrayList<>())
                .build();
    }

    @Test
    void save_shouldReturn201WithCreatedOrder() throws Exception {
        Order saved = buildOrder(1L, OrderStatus.PENDING);
        when(createOrder.execute(any(Order.class), eq("andy@test.com"))).thenReturn(saved);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderItems\":[]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void getAll_shouldReturn200WithOrderList() throws Exception {
        Order order1 = buildOrder(1L, OrderStatus.PENDING);
        Order order2 = buildOrder(2L, OrderStatus.PAID);
        when(getOrders.getAll()).thenReturn(List.of(order1, order2));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getById_shouldReturn200WhenOrderExists() throws Exception {
        Order order = buildOrder(1L, OrderStatus.PENDING);
        when(getOrders.getById(1L)).thenReturn(Optional.of(order));

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getById_shouldReturn404WhenOrderNotFound() throws Exception {
        when(getOrders.getById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/orders/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByUserEmail_shouldReturn200WithUserOrders() throws Exception {
        Order order = buildOrder(1L, OrderStatus.PENDING);
        when(getOrders.getByUserEmail("andy@test.com")).thenReturn(List.of(order));

        mockMvc.perform(get("/api/orders/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void updateStatus_shouldReturn200WhenOrderIsUpdated() throws Exception {
        Order updated = buildOrder(1L, OrderStatus.PAID);
        when(updateOrderStatus.execute(1L, OrderStatus.PAID)).thenReturn(Optional.of(updated));

        mockMvc.perform(put("/api/orders/1/status")
                        .param("status", "PAID"))
                .andExpect(status().isOk());
    }

    @Test
    void updateStatus_shouldReturn204WhenOrderCannotBeUpdated() throws Exception {
        when(updateOrderStatus.execute(1L, OrderStatus.CANCELLED)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/orders/1/status")
                        .param("status", "CANCELLED"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getById_shouldIncludeOrderItemsInResponse() throws Exception {
        Product reishi = Product.builder().id(1L).name("Reishi").build();
        OrderItem item = new OrderItem();
        item.setId(1L);
        item.setProduct(reishi);
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("25.00"));

        Order orderWithItems = Order.builder()
                .id(1L).user(user)
                .totalPrice(new BigDecimal("50.00"))
                .status(OrderStatus.PENDING)
                .createdAt(null)
                .orderItems(new ArrayList<>(List.of(item)))
                .build();

        when(getOrders.getById(1L)).thenReturn(Optional.of(orderWithItems));

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].productName").value("Reishi"))
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }
}
