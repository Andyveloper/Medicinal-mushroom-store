package com.setas.setas_backend.infrastructure.web;

import com.setas.setas_backend.config.StripeService;
import com.setas.setas_backend.domain.port.in.order.IReduceStock;
import com.setas.setas_backend.domain.port.in.order.IUpdateOrderStatus;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class WebhookControllerTest {

    @Mock private StripeService stripeService;
    @Mock private IUpdateOrderStatus updateOrderStatus;
    @Mock private IReduceStock reduceStock;

    @InjectMocks
    private WebhookController webhookController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(webhookController).build();
    }

    @Test
    void handleStripeWebhook_shouldReturn200ForUnhandledEventType() throws Exception {
        Event event = mock(Event.class);
        when(event.getType()).thenReturn("payment_intent.created");
        when(stripeService.constructWebhookEvent(any(), any())).thenReturn(event);

        mockMvc.perform(post("/api/webhooks/stripe")
                        .header("Stripe-Signature", "sig_test")
                        .content("{}")
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    void handleStripeWebhook_shouldReturn400WhenSignatureVerificationFails() throws Exception {
        when(stripeService.constructWebhookEvent(any(), any()))
                .thenThrow(mock(StripeException.class));

        mockMvc.perform(post("/api/webhooks/stripe")
                        .header("Stripe-Signature", "invalid_sig")
                        .content("{}")
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }
}
