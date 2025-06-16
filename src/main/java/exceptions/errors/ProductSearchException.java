package exceptions.errors;

public class ProductSearchException extends RuntimeException {
    public ProductSearchException(String message) {
        super(message);
    }

    public ProductSearchException(String message, Throwable cause) {
        super(message, cause);
    }
}