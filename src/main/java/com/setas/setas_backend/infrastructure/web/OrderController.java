package com.setas.setas_backend.infrastructure.web;

import com.setas.setas_backend.domain.model.Order;
import com.setas.setas_backend.domain.model.OrderStatus;
import com.setas.setas_backend.domain.port.in.order.ICreateOrder;
import com.setas.setas_backend.domain.port.in.order.IGetOrders;
import com.setas.setas_backend.domain.port.in.order.IUpdateOrderStatus;
import com.setas.setas_backend.infrastructure.web.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final ICreateOrder createOrder;
    private final IGetOrders getOrders;
    private final IUpdateOrderStatus updateOrderStatus;

    @PostMapping
    public ResponseEntity<OrderResponse> save(@RequestBody Order order) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assert authentication != null;
        String email = (String) authentication.getPrincipal();
        Order created = createOrder.execute(order, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(new OrderResponse(created));
    }

    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<OrderResponse> getById(@PathVariable Long id) {
        return getOrders.getById(id)
                .map(order -> ResponseEntity.status(HttpStatus.OK)
                        .body(new OrderResponse(order)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @Transactional
    public ResponseEntity<List<OrderResponse>> getByUserId(@PathVariable Long userId) {
        List<OrderResponse> found = getOrders.getByUserId(userId).stream().map(OrderResponse::new).toList();
        return ResponseEntity.ok(found);
    }

    @PutMapping("/{orderId}/status")
    @Transactional
    public ResponseEntity<Order> updateStatus(@PathVariable Long orderId, @RequestParam OrderStatus status) {
        Optional<Order> updated = updateOrderStatus.execute(orderId, status);
        return updated.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }
}
