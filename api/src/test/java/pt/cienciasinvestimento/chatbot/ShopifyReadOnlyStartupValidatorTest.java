package pt.cienciasinvestimento.chatbot;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import pt.cienciasinvestimento.chatbot.integration.shopify.ReadOnlyOperationException;
import pt.cienciasinvestimento.chatbot.integration.shopify.ShopifyProperties;
import pt.cienciasinvestimento.chatbot.integration.shopify.ShopifyReadOnlyGuard;
import pt.cienciasinvestimento.chatbot.integration.shopify.ShopifyReadOnlyStartupValidator;

class ShopifyReadOnlyStartupValidatorTest {

    @Test
    void startsWhenShopifyScopesAreReadOnly() {
        ShopifyProperties properties = new ShopifyProperties();
        properties.setAllowedAdminApiScopes(List.of("read_customers", "read_orders"));

        assertThatCode(() -> new ShopifyReadOnlyStartupValidator(properties, new ShopifyReadOnlyGuard()))
                .doesNotThrowAnyException();
    }

    @Test
    void failsStartupWhenShopifyScopesAllowWrites() {
        ShopifyProperties properties = new ShopifyProperties();
        properties.setAllowedAdminApiScopes(List.of("read_orders", "write_orders"));

        assertThatThrownBy(() -> new ShopifyReadOnlyStartupValidator(properties, new ShopifyReadOnlyGuard()))
                .isInstanceOf(ReadOnlyOperationException.class)
                .hasMessageContaining("write_orders");
    }
}
