package pt.cienciasinvestimento.chatbot.integration.shopify;

public class ReadOnlyOperationException extends RuntimeException {

    public ReadOnlyOperationException(String message) {
        super(message);
    }
}
