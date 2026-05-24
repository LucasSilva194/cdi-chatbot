package pt.cienciasinvestimento.chatbot.integration.shopify;

import java.util.List;
import java.util.Optional;

/**
 * Contrato exclusivamente de consulta. Nao adicionar metodos para cancelar,
 * reembolsar, editar encomendas, alterar subscricoes ou escrever dados externos.
 */
public interface ShopifyOrderService {

    List<ShopifyOrderSummary> findOrdersByCustomerEmail(String email);

    Optional<ShopifyOrderSummary> findPaidOrderForProduct(String email, String productHandle);
}
