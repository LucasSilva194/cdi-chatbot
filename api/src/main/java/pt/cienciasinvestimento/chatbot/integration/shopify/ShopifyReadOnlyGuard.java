package pt.cienciasinvestimento.chatbot.integration.shopify;

import java.util.Collection;
import java.util.Locale;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class ShopifyReadOnlyGuard {

    private static final Pattern GRAPHQL_MUTATION = Pattern.compile("(?is)(^|\\s)mutation(\\s|\\{|\\()");

    public void assertQueryOnly(String graphqlDocument) {
        if (graphqlDocument == null || graphqlDocument.isBlank()) {
            throw new ReadOnlyOperationException("Documento GraphQL vazio.");
        }

        if (GRAPHQL_MUTATION.matcher(graphqlDocument).find()) {
            throw new ReadOnlyOperationException("A integracao Shopify e estritamente read-only. Mutations nao sao permitidas.");
        }
    }

    public void assertReadOnlyScopes(Collection<String> scopes) {
        if (scopes == null || scopes.isEmpty()) {
            throw new ReadOnlyOperationException("A integracao Shopify precisa de scopes de leitura explicitos.");
        }

        for (String scope : scopes) {
            String normalizedScope = scope == null ? "" : scope.trim().toLowerCase(Locale.ROOT);
            if (!normalizedScope.startsWith("read_")) {
                throw new ReadOnlyOperationException(
                        "Scope Shopify nao permitido em modo read-only: " + scope
                );
            }
        }
    }
}
