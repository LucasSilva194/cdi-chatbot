package pt.cienciasinvestimento.chatbot.integration.shopify;

import java.time.Instant;

public record ShopifyOrderSummary(
        String shopifyOrderId,
        String productTitle,
        String productHandle,
        PaymentStatus paymentStatus,
        Instant createdAt
) {
}
