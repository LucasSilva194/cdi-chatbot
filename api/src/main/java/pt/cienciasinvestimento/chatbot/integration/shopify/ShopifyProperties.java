package pt.cienciasinvestimento.chatbot.integration.shopify;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.shopify")
public class ShopifyProperties {

    private String storeDomain;
    private String adminAccessToken;
    private List<String> allowedAdminApiScopes = new ArrayList<>(
            List.of("read_customers", "read_orders", "read_products")
    );

    public String getStoreDomain() {
        return storeDomain;
    }

    public void setStoreDomain(String storeDomain) {
        this.storeDomain = storeDomain;
    }

    public String getAdminAccessToken() {
        return adminAccessToken;
    }

    public void setAdminAccessToken(String adminAccessToken) {
        this.adminAccessToken = adminAccessToken;
    }

    public List<String> getAllowedAdminApiScopes() {
        return allowedAdminApiScopes;
    }

    public void setAllowedAdminApiScopes(List<String> allowedAdminApiScopes) {
        this.allowedAdminApiScopes = allowedAdminApiScopes;
    }
}
