package exceptions.errors;

public class ProductIndexException extends RuntimeException {
    public ProductIndexException(String message, Throwable cause) {
        super(message, cause);
    }
}