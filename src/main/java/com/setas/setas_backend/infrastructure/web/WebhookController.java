package com.setas.setas_backend.infrastructure.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.setas.setas_backend.config.StripeService;
import com.setas.setas_backend.domain.model.OrderStatus;
import com.setas.setas_backend.domain.port.in.order.IReduceStock;
import com.setas.setas_backend.domain.port.in.order.IUpdateOrderStatus;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class WebhookController {
  private final StripeService stripeService;
  private final IUpdateOrderStatus updateOrderStatus;
  private final IReduceStock reduceStock;

  @PostMapping("/stripe")
  public ResponseEntity<Void> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
    try {
      Event event = stripeService.constructWebhookEvent(payload, sigHeader);
      if ("payment_intent.succeeded".equals(event.getType())) {
        try {
          String rawJason = event.getDataObjectDeserializer().getRawJson();
          ObjectMapper objectMapper = new ObjectMapper();
          JsonNode jsonNode = objectMapper.readTree(rawJason);
          Long orderId = Long.parseLong(jsonNode.get("metadata").get("orderId").asText());
          updateOrderStatus.execute(orderId, OrderStatus.PAID);
          reduceStock.execute(orderId);
        } catch (Exception e) {
          System.out.println("Error procesando pago: " + e.getMessage());
        }
      }
      return ResponseEntity.ok().build();
    } catch (StripeException e) {
      return ResponseEntity.badRequest().build();
    }
  }
}
