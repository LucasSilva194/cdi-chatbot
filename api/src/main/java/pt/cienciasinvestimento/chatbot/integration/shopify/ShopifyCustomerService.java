package pt.cienciasinvestimento.chatbot.integration.shopify;

import java.util.Optional;

public interface ShopifyCustomerService {

    Optional<ShopifyCustomerSummary> findCustomerByEmail(String email);
}
