package exceptions.errors;

public class ProductDeleteException extends RuntimeException {
    public ProductDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}