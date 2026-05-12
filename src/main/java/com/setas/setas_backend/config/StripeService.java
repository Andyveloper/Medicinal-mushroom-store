package com.setas.setas_backend.config;

import com.setas.setas_backend.domain.model.User;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StripeService {
  @Value("${stripe.secret.key}")
  private String secretKey;

  @Value("${stripe.webhook.secret}")
  private String webhookSecret;

  @PostConstruct
  public void init() {
    Stripe.apiKey = secretKey;
  }

  public String createPaymentIntent(BigDecimal amount, Long orderId, User customer) throws StripeException {

    PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
            .setAmount(amount.multiply(new BigDecimal(100)).longValue())
            .setCurrency("cop")
            .putMetadata("orderId", orderId.toString())
            .setReceiptEmail(customer.getEmail())
            .setAutomaticPaymentMethods(
                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                            .setEnabled(true)
                            .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                            .build())
            .build();

    PaymentIntent paymentIntent = PaymentIntent.create(params);
    return paymentIntent.getClientSecret();
  }

  public Event constructWebhookEvent(String payload, String sigHeader) throws StripeException {
    return Webhook.constructEvent(payload, sigHeader, webhookSecret);
  }

}
