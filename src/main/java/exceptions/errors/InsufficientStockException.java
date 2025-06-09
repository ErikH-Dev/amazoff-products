package exceptions.errors;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String productId, int requestedQuantity, int availableStock) { // Changed from int to String
        super("Insufficient stock for product with id " + productId + ": requested " + requestedQuantity + ", available " + availableStock);
    }
}