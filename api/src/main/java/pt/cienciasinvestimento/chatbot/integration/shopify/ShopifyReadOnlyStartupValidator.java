package pt.cienciasinvestimento.chatbot.integration.shopify;

import org.springframework.stereotype.Component;

@Component
public class ShopifyReadOnlyStartupValidator {

    public ShopifyReadOnlyStartupValidator(ShopifyProperties properties, ShopifyReadOnlyGuard guard) {
        guard.assertReadOnlyScopes(properties.getAllowedAdminApiScopes());
    }
}
