package pt.cienciasinvestimento.chatbot.integration.shopify;

import java.util.Optional;

/**
 * Contrato exclusivamente de consulta. Nao adicionar metodos que criem,
 * atualizem ou apaguem dados na Shopify ou noutro sistema externo.
 */
public interface ShopifyCustomerService {

    Optional<ShopifyCustomerSummary> findCustomerByEmail(String email);
}
