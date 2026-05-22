package pt.cienciasinvestimento.chatbot.integration.shopify;

import java.util.List;
import java.util.Optional;

public interface ShopifyOrderService {

    List<ShopifyOrderSummary> findOrdersByCustomerEmail(String email);

    Optional<ShopifyOrderSummary> findPaidOrderForProduct(String email, String productHandle);
}
