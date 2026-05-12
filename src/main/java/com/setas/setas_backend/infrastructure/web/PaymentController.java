package com.setas.setas_backend.infrastructure.web;

import com.setas.setas_backend.config.StripeService;
import com.setas.setas_backend.domain.model.Order;
import com.setas.setas_backend.domain.model.User;
import com.setas.setas_backend.domain.port.in.order.IGetOrders;
import com.setas.setas_backend.domain.port.in.order.IUpdateOrderPaymentIntent;
import com.setas.setas_backend.domain.port.in.user.IFindByEmailUser;
import com.setas.setas_backend.infrastructure.web.dto.PaymentResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

  private final StripeService stripeService;
  private final IGetOrders getOrders;
  private final IFindByEmailUser findByEmailUser;
  private final IUpdateOrderPaymentIntent updateOrderPaymentIntent;

  @PostMapping("/{orderId}")
  @Transactional
  public ResponseEntity<PaymentResponse> createPayment(@PathVariable Long orderId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    assert authentication != null;
    String email = (String) authentication.getPrincipal();

    User user = findByEmailUser.execute(email).orElseThrow(() -> new RuntimeException("User not found"));

    Order order = getOrders.getById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

    if (!order.getUser().getEmail().equals(email)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    try {
      if (order.getPaymentIntentId() != null) {
        PaymentIntent existing = PaymentIntent.retrieve(order.getPaymentIntentId());
        if ("requires_payment_method".equals(existing.getStatus())) {
          return ResponseEntity.ok(new PaymentResponse(existing.getClientSecret(), order.getId()));
        }
      }

      String clientSecret = stripeService.createPaymentIntent(order.getTotalPrice(), order.getId(), user);
      String paymentIntentId = clientSecret.split("_secret_")[0];
      updateOrderPaymentIntent.execute(orderId, paymentIntentId);

      return ResponseEntity.ok(new PaymentResponse(clientSecret, order.getId()));
    } catch (StripeException e) {
      System.out.println("Error creando PaymentIntent: " + e.getMessage());
      return ResponseEntity.internalServerError().build();
    }

  }
}
