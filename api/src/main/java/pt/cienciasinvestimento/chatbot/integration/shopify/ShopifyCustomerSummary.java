package pt.cienciasinvestimento.chatbot.integration.shopify;

public record ShopifyCustomerSummary(
        String shopifyCustomerId,
        String email,
        boolean verified
) {
}
