package pt.cienciasinvestimento.chatbot;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import pt.cienciasinvestimento.chatbot.integration.shopify.ReadOnlyOperationException;
import pt.cienciasinvestimento.chatbot.integration.shopify.ShopifyReadOnlyGuard;

class ShopifyReadOnlyGuardTest {

    private final ShopifyReadOnlyGuard guard = new ShopifyReadOnlyGuard();

    @Test
    void allowsGraphqlQueries() {
        assertThatCode(() -> guard.assertQueryOnly("""
                query CustomerByEmail($query: String!) {
                  customers(first: 1, query: $query) {
                    nodes {
                      id
                      email
                    }
                  }
                }
                """)).doesNotThrowAnyException();
    }

    @Test
    void blocksGraphqlMutations() {
        assertThatThrownBy(() -> guard.assertQueryOnly("""
                mutation CancelOrder($id: ID!) {
                  orderCancel(orderId: $id) {
                    job {
                      id
                    }
                  }
                }
                """))
                .isInstanceOf(ReadOnlyOperationException.class)
                .hasMessageContaining("read-only");
    }

    @Test
    void allowsOnlyReadScopes() {
        assertThatCode(() -> guard.assertReadOnlyScopes(List.of("read_customers", "read_orders", "read_products")))
                .doesNotThrowAnyException();

        assertThatThrownBy(() -> guard.assertReadOnlyScopes(List.of("read_orders", "write_orders")))
                .isInstanceOf(ReadOnlyOperationException.class)
                .hasMessageContaining("write_orders");
    }
}
